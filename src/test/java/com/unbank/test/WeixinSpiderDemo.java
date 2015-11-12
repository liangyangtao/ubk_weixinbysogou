package com.unbank.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.unbank.fetch.HttpClientBuilder;

public class WeixinSpiderDemo {

	private static RequestConfig requestConfig = RequestConfig.custom()
			.setSocketTimeout(30000).setConnectTimeout(30000)
			.setCircularRedirectsAllowed(true).setMaxRedirects(50).build();
	private static Log logger = LogFactory.getLog(WeixinSpiderDemo.class);
	private static BasicCookieStore cookieStore = new BasicCookieStore();

	public static void main(String[] args) {
		for (int i=0;i<100;i++) {
			System.out.println(Math.pow(2, 1));
		}
		
		
	}
	public static byte[] decode(final byte[] bytes) {
		return Base64.decodeBase64(bytes);
	}

	/**
	 * 二进制数据编码为BASE64字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static String encode(final byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}

	




}
