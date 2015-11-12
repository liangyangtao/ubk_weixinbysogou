package com.unbank.quartz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.unbank.Spider;
import com.unbank.spider.weixinbigV.WeixinGongZhonghaoReader;
import com.unbank.store.WeiXinStoreToPlantform;

@Component
public class SougouSpiderByChrome {

	public static WebDriver driver;
	private static Log logger = LogFactory.getLog(Spider.class);
	static {
		try {
			driver = fillWebDriver();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void spider() {

		List<String> openids = new WeixinGongZhonghaoReader()
				.readGongZhongHaoList();
		for (String url : openids) {
			try {
				driver.get(url);
				Thread.sleep((long) ((Math.random() * 60 + 10) * 1000));
				// waitForPageLoaded(driver);
				String html = driver.getPageSource();
				if (html.contains("请输入验证码")) {
					logger.info("已经被屏蔽");
					break;
				}
				List<WebElement> webElement = driver.findElements(By
						.className("wx-rb3"));
				boolean isSave = true;
				for (WebElement webElement2 : webElement) {
					// s-p
					WebElement timeElement = webElement2.findElement(By
							.className("s-p"));
					String time = timeElement.getAttribute("t");
					WebElement aWebElement = webElement2.findElement(By
							.className("news_lst_tab"));
					aWebElement.click();
					Thread.sleep((long) ((Math.random() * 60 + 10) * 1000));
					// waitForPageLoaded(driver);
					String currentWindow = driver.getWindowHandle();
					Set<String> handles = driver.getWindowHandles();
					Iterator<String> it = handles.iterator();

					while (it.hasNext()) {
						String handle = it.next();
						if (currentWindow.equals(handle)) {
							continue;
						}
						WebDriver window = driver.switchTo().window(handle);

						try {
							// Thread.sleep(10000);
							// waitForPageLoaded(window);
							String ccurrentUrl = window.getCurrentUrl();
							html = window.getPageSource();
							isSave = saveInformation(html, time, ccurrentUrl);

						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							window.close();
						}
					}
					driver.switchTo().window(currentWindow);
					if (!isSave) {
						break;
					}
				}
				Set<String> handles = driver.getWindowHandles();
				if (handles.size() <= 1) {

				} else {
					driver.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		// 循环一轮以后，关闭浏览器
	}

	@PostConstruct
	public static void login() {
		// 在这个基础上先登录
		String url = "http://weixin.sogou.com/";
		driver.get(url);
		try {
			Thread.sleep(60 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static WebDriver fillWebDriver() {
		System.setProperty("webdriver.chrome.driver",
				"phantomjs-1.9.7-windows/chromedriver.exe");
		WebDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		// long timeout = 30000;
		// TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		//
		// driver.manage().timeouts().pageLoadTimeout(timeout, timeUnit);
		// 查找元素的超时时间
		// driver.manage().timeouts().implicitlyWait(time, unit)
		// 执行javascript 的执行时间
		// driver.manage().timeouts().setScriptTimeout(time, unit);
		return driver;
	}

	public static void waitForPageLoaded(WebDriver driver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
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

	public boolean saveInformation(String html, String time, String ccurrentUrl) {
		if (SimpleBloomFilter.filter.contains(ccurrentUrl)) {
			return true;
		} else {
			SimpleBloomFilter.filter.add(ccurrentUrl);
		}
		Map<String, Object> information = new HashMap<String, Object>();
		Document document = Jsoup.parse(html, ccurrentUrl);
		// 解析 map
		Element titleElement = document.select("h2#activity-name").first();
		Element contentElement = document.select("#js_content").first();

		String content = contentElement.toString();
		content = formatContent(content, ccurrentUrl);
		information.put("title", titleElement.text().trim());
		information.put("url", ccurrentUrl);
		information.put("lastModified", time);
		information.put("content", content);
		return new WeiXinStoreToPlantform().saveWeixinBySql(information);
	}

	public static Date timestampToDate(String beginDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sd = sdf.format(new Date(Long.parseLong(beginDate) * 1000));
		Date date = null;
		try {
			date = sdf.parse(sd);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private static String formatContent(String content, String url) {
		Document document = Jsoup.parse(content, url);
		removeNoNeedElementsByCssQuery(document.body());
		formatElements(document);
		saveImage(document, url);
		content = document.toString();
		content = replaceStockCode(content);
		content = content.replace("<html><head></head><body>", "");
		content = content.replace("</body></html>", "");
		content = content.replace("'", "");
		return content;
	}

	public static void saveImage(Element maxTextElement, String url) {
		Elements elements = maxTextElement.select("img");
		for (Element element : elements) {
			if (element.attr("style").contains("display:none;")) {
				element.remove();
				continue;
			}
			String imgSrc = element.absUrl("src");
			if (imgSrc == null || imgSrc.trim().isEmpty()) {
				element.remove();
				continue;
			} else if (imgSrc.contains("data:image")) {
				imgSrc = element.absUrl("data-src");
			}
			String imgUrl = new WeixinImageFeatch().fetchImage(imgSrc);
			if (imgUrl != null && (!imgUrl.trim().isEmpty())) {
				element.attr("src", imgUrl);
				// 去掉除src 的所有属性
				Attributes attributes = element.attributes();
				for (Attribute attribute : attributes) {
					if (attribute.getKey().isEmpty()) {
						continue;
					} else if (attribute.getKey().equals("src")) {
						continue;
					} else {
						element.removeAttr(attribute.getKey());
					}
				}
			} else {
				element.remove();
			}
		}

	}

	// 去掉不需要的HTML标签
	public static void removeNoNeedElementsByCssQuery(Element contentElement) {
		String cssQuerys[] = new String[] { "script", "style", "textarea",
				"select", "noscript", "input", "em" };
		for (String string : cssQuerys) {
			removeNoNeedElement(contentElement, string);
		}
	} // 去掉不想要的html 标签

	public static void removeNoNeedElement(Element element, String cssQuery) {
		if (element == null) {
			return;
		}
		Elements elements = element.select(cssQuery);
		for (Element element2 : elements) {
			element2.remove();
		}
	}

	public static void formatElements(Element contentElement) {
		// 去重属性
		removeElementAttr(contentElement);
		Elements allElements = contentElement.children();
		for (Element element : allElements) {
			removeElementAttr(element);
			if (element != null) {
				formatElements(element);
			}

		}

	}

	// 移除所有的属性

	public static void removeElementAttr(Element element) {
		if (element == null) {
			return;
		}
		Attributes attributes = element.attributes();
		for (Attribute attribute : attributes) {
			if (attribute.getKey().isEmpty()) {
				continue;
			} else if (attribute.getKey().equals("align")
					&& attribute.getValue().equals("center")) {
				continue;
			} else if (attribute.getKey().equals("style")
					&& (attribute.getValue().toLowerCase()
							.contains("text-align: center"))) {
				continue;
			} else if (attribute.getKey().equals("rowspan")
					|| attribute.getKey().equals("colspan")
					|| attribute.getKey().equals("src")
					|| attribute.getKey().equals("data-src")) {
				continue;
			} else {
				element.removeAttr(attribute.getKey());
			}
		}
	}

	public static String replaceStockCode(String content) {
		try {
			content = content.replaceAll(">\\s{0,10}", ">");
			content = content.replaceAll(">\\s{0,10}(&nbsp; ){0,}", ">");
			content = content.replaceAll(">\\s{0,10}(&nbsp;){0,}", ">");
			content = content.replaceAll(">\\s{0,10} {0,}", ">");
			content = content.replaceAll(">\\s{0,10}  {0,}", ">");
			content = content.replaceAll(">\\s{0,10}", ">");
			content = content.replaceAll("\\s{0,10}<", "<");
			content = content.replaceAll("<p></p>", "");
			content = content.trim();
		} catch (Exception e) {
			e.printStackTrace();
			return content;
		}
		return content;
	}
}
