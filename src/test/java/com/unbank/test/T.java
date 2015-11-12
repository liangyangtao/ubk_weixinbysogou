package com.unbank.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Key;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.unbank.tools.MD5;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class T {
	public static void main(String[] args) {
		
		String c ="<p>　　黑龙江晨报讯省森工总局13日发布消息，16日10时，平山“皇家鹿苑草食动物园(驯养示范基地)”将对外正式开园。该动物园现有草食动物鹿科、牛科、马科、骆驼科、羊科等动物10余种，1000余头(只、匹";
		c =c.replaceAll(">　{0,10}", ">");
		System.out.println(c);
		// String json ="{\"button\":[{\"type\":\"click\"}]}";
		// System.out.println(json2XML(json));
		System.out.println(MD5.GetMD5Code("1"));
		System.exit(0);
		System.out.println(new Date().getTime());
		// 1439196169331
		System.out.println(System.currentTimeMillis());
		// 1441604645135551
//		System.out.println(dateToTimestamp("2015-09-07 13:44:05"));
//		System.out.println(timestampToDate("1441604633307942"));
		System.out.println(timestampToDate("1441665750"));
		String a = "http://hi.baidu.com/tag/java%20c%23%E4%BA%8B%E4%BE%8B/feeds";
		try {
			System.out.println(URLDecoder.decode(a, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String base64String = "p0OVDH8R4SHyUySb8E88hkJm8GF_McJfBfynRTbN8wj6jX5Z03wJ-mDtoIcqjEdAXvl8nOTxoMBWfQYxdWiGM2Q3JxMQ3374BGxUjBZZYkeoRt-_BFbND2cTZPpisuRbUUM3M8ttJkJYy-5x5In7jJFmExjqCxhpkyjFvwP6PuGcQ64lGQ2ZDMuqxplQrsbk";
		try {
			//Cookie: ABTEST=0|1441530751|v1; SUID=3689953DE518920A0000000055ECEC8C; YYID=7A4C8FD1EF9400E40DFA5BFFD82B0F5B; QQ_Sogou_Openid=7C31C211FF433218500EE0F097AE45FC,1441586588; IMEVER=7.7.0.6390; IPLOC=CN1100; CXID=1083518D469484E20C30FD7FEC2AA083; SNUID=7CC3DF764B4E5455263AD0784B591FDE
			String sKey = "7C31C211FF433218500EE0F097AE45FC";
			byte[] raw = sKey.getBytes();
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
			IvParameterSpec iv = new IvParameterSpec(
					"0000000000000000".getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(base64String.getBytes());
			for (byte b : encrypted) {
				System.out.println((char) b);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static String DecodeEcrypt(String sSrc, String sKey)
			throws Exception {
		if (sKey == null) {
			System.out.print("Key为空null");
			return null;
		}
		if (sKey.length() != 16) {
			System.out.print("Key长度不是16位");
			return null;
		}
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
		IvParameterSpec iv = new IvParameterSpec("0000000000000000".getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
		return Base64.encodeBase64String(encrypted);// 此处使用BAES64做转码功能，同时能起到2次加密的作用。
		// return Base64.de
	}

	public static String Encrypt(String sSrc, String sKey) throws Exception {
		if (sKey == null) {
			System.out.print("Key为空null");
			return null;
		}
		if (sKey.length() != 16) {
			System.out.print("Key长度不是16位");
			return null;
		}
		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
		IvParameterSpec iv = new IvParameterSpec("0000000000000000".getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes());
		return Base64.encodeBase64String(encrypted);// 此处使用BAES64做转码功能，同时能起到2次加密的作用。
		// return Base64.de
	}

	public static String timestampToDate(String beginDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(Long.parseLong(beginDate) * 1000));
		return sd;
	}

	public static long dateToTimestamp(String user_time) {
		long times = 0;
		try {
			times = Timestamp.valueOf(user_time).getTime() * 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (times == 0) {
			System.out.println("String转10位时间戳失败");
		}
		return times;
	}

	public static String json2XML(String json) {
		JSONObject jobj = JSONObject.fromObject(json);
		String xml = new XMLSerializer().write(jobj);
		return xml;
	}
}
