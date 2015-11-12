package com.unbank.fetch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class SougouWeiXinFetcher extends Fetcher {

	private static Log logger = LogFactory.getLog(SougouWeiXinFetcher.class);

	public static SougouWeiXinFetcher fetcher = SougouWeiXinFetcher
			.getInstance();

	public synchronized static SougouWeiXinFetcher getInstance() {
		if (fetcher == null) {
			fetcher = new SougouWeiXinFetcher();
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
			HttpClientBuilder httpClientBuilder = new HttpClientBuilder(false,
					poolingHttpClientConnectionManager, cookieStore);
			httpClient = httpClientBuilder.getHttpClient();
		}
		return fetcher;
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
		httpGet.setHeader("Host", "weixin.sogou.com");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Referer", "http://weixin.sogou.com/");
		httpGet.setHeader("Cache-Control", "max-age=0");
		httpGet.setHeader("Cookie", cookie);
	}

}
