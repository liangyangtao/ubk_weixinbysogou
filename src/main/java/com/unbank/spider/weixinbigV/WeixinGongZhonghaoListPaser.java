package com.unbank.spider.weixinbigV;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unbank.fetch.SougouWeiXinFetcher;

public class WeixinGongZhonghaoListPaser {
	private static Log logger = LogFactory
			.getLog(WeixinGongZhonghaoListPaser.class);

	public String getXmlPage(String openid, String page) {
		String newHtml = null;
		try {
			String t = new Date().getTime() + "";
			String ekv = "";
			String eqs = "";
			String weinxinSougouUrl = "http://weixin.sogou.com/";
			SougouWeiXinFetcher sougouWeiXinFetcher = SougouWeiXinFetcher
					.getInstance();
			String shouhtml =sougouWeiXinFetcher.getHtmlWithCookie(weinxinSougouUrl);
			String openidUrl = "http://weixin.sogou.com/gzh?openid=" + openid;
			String html = sougouWeiXinFetcher.getHtmlWithCookie(openidUrl);
			
			List<String> keys = getKey(html);
			ekv = keys.get(1);
			eqs = processEqs(keys.get(0), openid, "sougou");
			String url = "http://weixin.sogou.com/gzhjs?cb=sogou.weixin.gzhcb&openid="
					+ openid
					+ "&eqs="
					+ eqs
					+ "&ekv="
					+ ekv
					+ "&page="
					+ page
					+ "&t=" + t;
			newHtml = sougouWeiXinFetcher.getHtmlWithCookie(url);
		} catch (Exception e) {
			logger.info("获取搜狗列表页失败", e);
			e.printStackTrace();
		}
		return newHtml;
	}

	public static List<String> getKey(String html) {
		String resultString = ereg(
				"SogouEncrypt.setKv\\(\"(\\w+)\",\"(\\d)\"\\)", html);
		String key = StringUtils.substringBetween(resultString, "setKv(\"",
				"\",\"");
		String level = StringUtils.substringBetween(resultString, "\",\"",
				"\")");
		List<String> result = new ArrayList<String>();
		result.add(key);
		result.add(level);
		return result;
	}

	public static final String ereg(String pattern, String str) {
		try {
			Pattern p = Pattern.compile(pattern, Pattern.DOTALL);
			Matcher m = p.matcher(str);
			if (m.find()) {
				return m.group();
			}
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String f(String h) {
		if (h == null || h == "") {
			return "sogou";
		}
		if (h.length() > 5) {
			h = h.substring(h.length() - 5, 5);
		}
		while (h.length() < 5) {
			h += "s";
		}
		return h;
	}

	public static String g(String i) {
		if (i == null || i == "") {
			return "sogou";
		}
		String h[] = i.split("-");
		if (h.length > 2) {
			return f(h[2]);
		} else {
			return f(h[0]);
		}
	}

	public static String e(String k, String o) {
		if (k == "" || o == "") {
			return "";
		}
		if (o.length() != 5) {
			o = "sogou";
		}
		int n = k.length();
		int l = 0;
		String h = "";
		for (int m = 0; m < n; m++) {
			h += k.charAt(m);

			if (m == Math.pow(2, l) && l < 5) {
				h += o.charAt(l++);
			}
		}
		return h;
	}

	public static String processEqs(String c, String p, String n) {
		String eqs = null;
		try {
			if (c == null || c == "" || c.length() != 11) {
				return "openid=" + URLEncoder.encode(p, "utf-8") + "&hdq=" + n;
			}
			String i = c;
			String m = g(n);
			i += m;
			if (i == null || i == "" || i.length() != 16) {
				return "openid=" + URLEncoder.encode(p, "utf-8") + "&hdq=" + n;
			}
			String o = p + "hdq=" + n;
			String k = Encrypt(o, i);
			String r = e(k.toString(), m);
			eqs = URLEncoder.encode(r, "utf-8");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return eqs;

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
	}

	public String getWinxinEntity(String openid, String page) {
		String html = getXmlPage(openid, page);
		return html;
	}

}
