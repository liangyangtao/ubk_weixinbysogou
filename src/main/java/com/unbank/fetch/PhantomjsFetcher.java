package com.unbank.fetch;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PhantomjsFetcher {

	private static Log logger = LogFactory.getLog(PhantomjsFetcher.class);

	private static WebDriver driver;

	public static synchronized WebDriver getInstenceDriver() {
		if (driver == null) {
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setJavascriptEnabled(true);
			caps.setCapability(
					PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					"phantomjs-1.9.7-windows/phantomjs.exe");
			driver = new PhantomJSDriver(caps);
			long timeout = 5000;
			TimeUnit timeUnit = TimeUnit.MILLISECONDS;
			driver.manage().timeouts().pageLoadTimeout(timeout, timeUnit);
		}
		return driver;
	}

	public PhantomjsFetcher() {
		getInstenceDriver();
	}

	public static String get(String url) {
		getInstenceDriver();
		String html = null;
		try {
			driver.get(url);
			waitForPageLoaded(driver);
			html = driver.getPageSource();

		} catch (Exception e) {

			if (e instanceof org.openqa.selenium.TimeoutException) {
				System.out.println(((JavascriptExecutor) driver)
						.executeScript("return document.readyState"));
				logger.info(url + "       " + "读取超时");
			} else {
				e.printStackTrace();
			}
		} finally {
			html = driver.getPageSource();
		}
		return html;
	}

	public void scroll() {
		((JavascriptExecutor) driver)
				.executeScript("window.scrollTo(0,document.body.scrollHeight)");
	}

	public static void waitForPageLoaded(WebDriver driver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				// System.out.println(((JavascriptExecutor) driver)
				// .executeScript("return document.readyState"));
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};
		Wait<WebDriver> wait = new WebDriverWait(driver, 5000);
		try {
			wait.until(expectation);
		} catch (Throwable error) {
			logger.info(error);
		}
	}

	public String get(String url, String string) {

		return null;
	}

	public void setProxy(String proxyIp, String proxyPort) {
		closeDriver();
		String PROXY = proxyIp + ":" + proxyPort;
		org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
		proxy.setHttpProxy(PROXY).setFtpProxy(PROXY).setSslProxy(PROXY);
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setJavascriptEnabled(true);
		caps.setCapability(CapabilityType.PROXY, proxy);
		caps.setCapability(
				PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"phantomjs-1.9.7-windows/phantomjs.exe");
		driver = new PhantomJSDriver(caps);
		long timeout = 5000;
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		driver.manage().timeouts().pageLoadTimeout(timeout, timeUnit);
	}

	public static void closeDriver() {
		if (driver != null) {
			driver.quit();
		}

	};
}
