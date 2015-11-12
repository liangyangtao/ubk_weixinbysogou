package com.unbank.queue;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.unbank.fetch.Fetcher;
import com.unbank.store.WeiXinStoreBySql;
import com.unbank.store.WeiXinStoreToPlantform;

public class InformationConsume extends BaseQueue implements Runnable {
	protected LinkedBlockingQueue<Object> informationQueue;

	public InformationConsume(LinkedBlockingQueue<Object> informationQueue) {
		this.informationQueue = informationQueue;
	}

	@Override
	public void run() {
		while (true) {
			try {
				if (informationQueue.size() > 0) {
					Map<String, Object> information = null;
					information = (Map<String, Object>) take(informationQueue);
					if (information != null) {
						consumeInformation(information);
					}
				}
				sleeping(30000);
			} catch (Exception e) {
				logger.info("", e);
				continue;
			}

		}
	}

	public void consumeInformation(Map<String, Object> information) {
		String url = (String) information.get("url");
		Document document = Fetcher.getInstance().getDoument(url);
		Element contentElement = document.select("#js_content").first();
		String content = contentElement.toString();
		content = formatContent(content);
		information.put("content", content);
		new WeiXinStoreToPlantform().saveWeixinBySql(information);
	}

	private String formatContent(String content) {
		Document document = Jsoup.parse(content);
		removeNoNeedElementsByCssQuery(document.body());
		formatElements(document);
		content = document.toString();
		content = replaceStockCode(content);
		content = content.replace("<html><head></head><body>", "");
		content = content.replace("</body></html>", "");
		content = content.replace("'", "");
		return content;
	}

	// 去掉不需要的HTML标签
	public void removeNoNeedElementsByCssQuery(Element contentElement) {
		String cssQuerys[] = new String[] { "script", "style", "textarea",
				"select", "noscript", "input", "em" };
		for (String string : cssQuerys) {
			removeNoNeedElement(contentElement, string);
		}
	} // 去掉不想要的html 标签

	public void removeNoNeedElement(Element element, String cssQuery) {
		if (element == null) {
			return;
		}
		Elements elements = element.select(cssQuery);
		for (Element element2 : elements) {
			element2.remove();
		}
	}

	public void formatElements(Element contentElement) {
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

	public void removeElementAttr(Element element) {
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
					|| attribute.getKey().equals("src")) {
				continue;
			} else {
				element.removeAttr(attribute.getKey());
			}
		}
	}

	public String replaceStockCode(String content) {
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
