package com.unbank.test;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.unbank.fetch.HttpClientBuilder;

public class SougouTest {
	private static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(30000).setConnectTimeout(30000).build();
	private static BasicCookieStore cookieStore = new BasicCookieStore();
	private static Log logger = LogFactory.getLog(SougouTest.class);
	private static final String _DEFLAUT_CHARSET = "utf-8";

	public static String getCookiesString() {
		List<Cookie> cookies = cookieStore.getCookies();
		StringBuffer sb = new StringBuffer();
		if (cookies != null) {
			for (Cookie c : cookies) {
				sb.append(c.getName() + "=" + c.getValue() + ";");
			}
		}
		return sb.toString();
	}

	private static void fillHeader(String url, HttpGet httpGet, String cookie) {
		httpGet.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0");
		httpGet.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.setHeader("Accept-Language",
				"zh-CN,zh;q=0.8,en-us;q=0.8,en;q=0.6");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate,sdch");
		httpGet.setHeader("Host", "weixin.sogou.com");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Referer", "http://weixin.sogou.com");
		httpGet.setHeader("Cache-Control", "no-cache");
		httpGet.setHeader("Pragma", "no-cache");
		httpGet.setHeader("Cookie", cookie);
	}

	public static String getHtml(CloseableHttpClient httpClient, String url,
			String charset, String cookie) {
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		String useCharset = charset;
		HttpGet httpGet = new HttpGet(url);
		fillHeader(url, httpGet, cookie);
		httpGet.setConfig(requestConfig);
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet,
					context);
			try {
				HttpEntity entity = response.getEntity();
				return EntityUtils.toString(entity, useCharset);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

		}
		return null;
	}

	public static void main(String[] args) {
		// WEIXIN_KEY = 'http://weixin.sogou.com/gzh?openid={id}'
		// WEIXIN_COOKIE = 'http://weixin.sogou.com/weixin?query={q}'
		// WEIXIN_URL =
		// 'http://weixin.sogou.com/gzhjs?cb=sogou.weixin.gzhcb&openid={id}&eqs={eqs}&ekv={ekv}&page=1&t={t}'
		String openid = "oIWsFt0B7LsVbUCMpgksNY8tqIno";
		String t = new Date().getTime() + "";
		String ekv = "";
		String page = "1";
		String eqs = "";

		PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
		BasicCookieStore cookieStore = new BasicCookieStore();
		HttpClientBuilder httpClientBuilder = new HttpClientBuilder(false,
				poolingHttpClientConnectionManager, cookieStore);
		CloseableHttpClient httpClient = httpClientBuilder.getHttpClient();
		// 1 访问搜狗微信首页获取Cookie
		String weinxinSougouUrl = "http://weixin.sogou.com/";
		// System.out.println(getCookiesString());
		String html = getHtml(httpClient, weinxinSougouUrl, "utf-8",
				getCookiesString());
		System.out.println(getCookiesString());
		String openidUrl = "http://weixin.sogou.com/gzh?openid=" + openid;
		html = getHtml(httpClient, openidUrl, "utf-8", getCookiesString());
		System.out.println(getCookiesString());
		System.out.println(html);
		List<String> keys = getKey(html);
		// String key = self.mc.get('key')
		// String key = null;
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
		System.out.println(url);
		String newHtml = getHtml(httpClient, url, "utf-8", getCookiesString());
		System.out.println(newHtml);
		String temp = newHtml.replaceAll("\\n{0,10}", "");
		System.out.println(temp);
		temp = StringUtils.substringBetween(temp, "sogou.weixin.gzhcb(",
				")<!--STATUS");
		JSONObject jsonObject = JSONObject.fromObject(temp);
		Object jsonArray = jsonObject.get("items");
		JSONArray jsonArray2 = JSONArray.fromObject(jsonArray);
		for (Object object : jsonArray2) {

			Map<String, Object> a = parser((String) object);
			Set<String> set = a.keySet();
			for (String string : set) {
				System.out.println(string + " =============" + a.get(string));
				if (string.trim().equals("url")) {
					String childUrl = "http://weixin.sogou.com"
							///websearch/art.jsp?sg=-kjBTUzEYBCugZaYIIHBK5rxbIXuBN35O8SmNhppU-9SaXxNRaESgYOgkoOQH9ol9DIkyuQNoPG137BuNNMoz9z4igfwfhhi6c6n_eQgbditT_G-SFVK5Q..&url=p0OVDH8R4SHyUySb8E88hkJm8GF_McJfBfynRTbN8wj6jX5Z03wJ-mDtoIcqjEdAXvl8nOTxoMBWfQYxdWiGM2Q3JxMQ3374BGxUjBZZYkeoRt-_BFbND2cTZPpisuRbUUM3M8ttJkJYy-5x5In7jJFmExjqCxhpkyjFvwP6PuGcQ64lGQ2ZDMuqxplQrsbk

							+ a.get(string);
				    System.out.println(childUrl);
//					newHtml = getHtml(httpClient, childUrl, "utf-8",
//							getCookiesString());
//					System.out.println(newHtml);
				}

			}
			// System.out.println(object);
		}

	}

	public static Map<String, Object> parser(String object) {
		Map<String, Object> map = new HashMap<String, Object>();
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(new StringReader(object));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		Element itemElement = root.element("item");
		Element displayElement = itemElement.element("display");
		Iterator iterator = displayElement.elementIterator();
		while (iterator.hasNext()) {
			Element element = (Element) iterator.next();
			String name = element.getName();
			String value = element.getTextTrim();
			map.put(name, value);
		}
		return map;
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
		// (SogouEncrypt.setKv("8d07ae077be","7"),,SogouEncrypt.encryptquery("oIWsFt0Xx_eFgPwMPErWHEb3YjOs","sogou"))
		// StringBuffer sb = new StringBuffer();
		// sb.append("(" + key + "," + level + "," + setting + ")");
		// System.out.println(sb.toString());
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
		// DNsiog5gJG7+okNonDAOhu0+UVttW5GtlntpHogys+I96U5LEwRtlKD4fF1Oo5iTLwmDN
		// sHoAub6g%2FnUmoaxokU759syDn%2BnuePk1ENW0ifIyU1FEEFDRjon9hIc9Bs69zHsJR6P6z
		// HSsvoIZgM%2BypoWWQpKa8%2FukrFEccqzBM3kxjdHIJtd6SmEea%2FsbISLTPxNksDeINSWu%2Bl
		// sHoAub6g%2FnUmoaxokU759syDn%2BnuePk1ENW0ifIyU1FEEFDRjon9hIc9Bs69zHsJR6P6z
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
			// 8d07ae077be
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

}
