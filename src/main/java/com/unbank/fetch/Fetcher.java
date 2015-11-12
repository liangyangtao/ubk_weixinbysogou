package com.unbank.fetch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Fetcher {
	public static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(30000).setConnectTimeout(30000).build();
	public static BasicCookieStore cookieStore = new BasicCookieStore();
	public static CloseableHttpClient httpClient;
	public final String _DEFLAUT_CHARSET = "utf-8";
	public static Fetcher fetcher = Fetcher.getInstance();
	public static Log logger = LogFactory.getLog(Fetcher.class);

	public synchronized static Fetcher getInstance() {
		if (fetcher == null) {
			fetcher = new Fetcher();
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
			HttpClientBuilder httpClientBuilder = new HttpClientBuilder(false,
					poolingHttpClientConnectionManager, cookieStore);
			httpClient = httpClientBuilder.getHttpClient();
		}
		return fetcher;
	}

	public String inputStream2String(InputStream is, String charset) {
		String temp = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int i = -1;
			while ((i = is.read()) != -1) {
				baos.write(i);
			}
			temp = baos.toString(charset);
			if (temp.contains("???") || temp.contains("�")) {
				Pattern pattern = Pattern
						.compile("<meta[\\s\\S]*?charset=\"{0,1}(\\S+?)\"\\s{0,10}/{0,1}>");
				// .compile("<meta\\s+http-equiv=\"content-type\"\\s+content=\"[\\s\\S]*?charset=(\\S+?)\"\\s+/>");
				Matcher matcher = pattern.matcher(temp.toLowerCase());
				if (matcher.find()) {
					charset = matcher.group(1);
				} else {
					charset = "gbk";
				}
				temp = baos.toString(charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return temp;

	}

	public String getHtmlWithCookie(String url) {
		return getHtml(httpClient, url, getCookiesString());
	}

	public String getCookiesString() {
		List<Cookie> cookies = cookieStore.getCookies();
		StringBuffer sb = new StringBuffer();
		if (cookies != null) {
			for (Cookie c : cookies) {
				sb.append(c.getName() + "=" + c.getValue() + ";");
			}
		}
		return sb.toString();
	}

	public void fillHeaderWithCookie(String url, HttpGet httpGet, String cookie) {
		httpGet.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		httpGet.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.setHeader("Accept-Language",
				"zh-CN,zh;q=0.8,en-us;q=0.8,en;q=0.6");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate,sdch");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Cache-Control", "max-age=0");
		httpGet.setHeader("Cookie", cookie);
	}

	public String getHtml(CloseableHttpClient httpClient, String url,
			String cookie) {
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);
		HttpGet httpGet = new HttpGet(url);
		fillHeaderWithCookie(url, httpGet, cookie);
		httpGet.setConfig(requestConfig);
		String chartset = null;
		String result = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet,
					context);
			try {
				Header heads[] = response.getAllHeaders();
				for (Header header : heads) {
					if (header.getValue().toLowerCase().contains("charset")) {
						Pattern pattern = Pattern
								.compile("charset=[^\\w]?([-\\w]+)");
						Matcher matcher = pattern.matcher(header.getValue());
						if (matcher.find()) {
							chartset = matcher.group(1);
						}
					}
				}
				if (chartset == null) {
					chartset = "utf-8";
				} else {
					if (chartset.equals("gbk2312")) {
						chartset = "gbk";
					}
				}
				InputStream inputStream = response.getEntity().getContent();
				result = inputStream2String(inputStream, chartset);
			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Document getDoument(String url) {
		String html = getHtmlWithCookie(url);
		if (html == null) {
			return null;
		}
		Document document = Jsoup.parse(html, url);
		return document;
	}

	public InputStream getImage(String url) {
		return getImage(httpClient, url, getCookiesString());
	}

	public InputStream getImage(CloseableHttpClient httpClient, String url,
			String cookie) {
		HttpClientContext context = HttpClientContext.create();
		HttpGet httpGet = new HttpGet(url);
		fillHeaderWithCookie(url, httpGet, cookie);
		httpGet.setConfig(requestConfig);
		InputStream inputStream = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet,
					context);
			try {
				HttpEntity entity = response.getEntity();
				inputStream = new ByteArrayInputStream(
						EntityUtils.toByteArray(entity));

			} finally {
				response.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputStream;
	}

}
