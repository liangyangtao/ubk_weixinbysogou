package com.unbank.fetch;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.protocol.HttpContext;

public class HttpClientBuilder {

	private CloseableHttpClient httpClient;
	private BasicCookieStore cookieStore;
	private PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;

	public HttpClientBuilder(
			boolean ssl,
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
			BasicCookieStore cookieStore) {
		this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
		this.cookieStore = cookieStore;
		httpClientInit(ssl);
	}

	public void httpClientInit(boolean ssl) {
		try {
			poolingHttpClientConnectionManager.setMaxTotal(100000);// 连接池最大并发连接数
			poolingHttpClientConnectionManager.setDefaultMaxPerRoute(1000);// 单路由最大并发数
			ConnectionKeepAliveStrategy keepAliveStrat = new DefaultConnectionKeepAliveStrategy() {

				@Override
				public long getKeepAliveDuration(HttpResponse response,
						HttpContext context) {
					long keepAlive = super.getKeepAliveDuration(response,
							context);
					if (keepAlive == -1) {
						// 如果服务器没有设置keep-alive这个参数，我们就把它设置成5秒
						keepAlive = 5000;
					}
					return keepAlive;
				}

			};
			// 重试机制
			HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
				public boolean retryRequest(IOException exception,
						int executionCount, HttpContext context) {
					if (executionCount >= 5) {
						// 如果已经重试了5次，就放弃
						return false;
					}
					if (exception instanceof InterruptedIOException) {
						// 超时
						return false;
					}
					if (exception instanceof UnknownHostException) {
						// 目标服务器不可达
						return false;
					}
					if (exception instanceof ConnectTimeoutException) {
						// 连接被拒绝
						return false;
					}
					if (exception instanceof SSLException) {
						// ssl握手异常
						return false;
					}
					HttpClientContext clientContext = HttpClientContext
							.adapt(context);
					HttpRequest request = clientContext.getRequest();
					boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
					if (idempotent) {
						// 如果请求是幂等的，就再次尝试
						return true;
					}
					return false;
				}

			};
			CookieSpecProvider easySpecProvider = new CookieSpecProvider() {

				public CookieSpec create(HttpContext context) {

					return new BrowserCompatSpec() {
						@Override
						public void validate(Cookie cookie, CookieOrigin origin)
								throws MalformedCookieException {
							// Oh, I am easy
						}
					};
				}

			};

			RegistryBuilder
					.<CookieSpecProvider> create()
					.register(CookieSpecs.BEST_MATCH,
							new BestMatchSpecFactory())
					.register(CookieSpecs.BROWSER_COMPATIBILITY,
							new BrowserCompatSpecFactory())
					.register("easy", easySpecProvider).build();

			RequestConfig globalConfig = RequestConfig.custom()
					.setCookieSpec("easy").build();

			LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

			if (ssl) {
				SSLContext sslContext = new SSLContextBuilder()
						.loadTrustMaterial(null, new TrustStrategy() {
							// 信任所有
							public boolean isTrusted(X509Certificate[] chain,
									String authType)
									throws CertificateException {
								return true;
							}
						}).build();
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
						sslContext);
				httpClient = HttpClients
						.custom()
						.setConnectionManager(
								poolingHttpClientConnectionManager)
						.setKeepAliveStrategy(keepAliveStrat)
						.setRetryHandler(retryHandler)
						.setDefaultRequestConfig(globalConfig)
						.setDefaultCookieStore(cookieStore)
						.setRedirectStrategy(redirectStrategy)
						.setSSLSocketFactory(sslsf).build();

			} else {

				httpClient = HttpClients
						.custom()
						.setConnectionManager(
								poolingHttpClientConnectionManager)
						.setKeepAliveStrategy(keepAliveStrat)
						.setRetryHandler(retryHandler)
						.setDefaultRequestConfig(globalConfig)
						.setDefaultCookieStore(cookieStore)
						.setRedirectStrategy(redirectStrategy).build();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public BasicCookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(BasicCookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager() {
		return poolingHttpClientConnectionManager;
	}

	public void setPoolingHttpClientConnectionManager(
			PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
		this.poolingHttpClientConnectionManager = poolingHttpClientConnectionManager;
	}

}
