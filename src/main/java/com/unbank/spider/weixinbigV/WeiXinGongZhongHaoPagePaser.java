package com.unbank.spider.weixinbigV;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;

import com.unbank.redis.JedisUtil;
import com.unbank.tools.MD5;

public class WeiXinGongZhongHaoPagePaser {
	private static Log logger = LogFactory
			.getLog(WeiXinGongZhongHaoPagePaser.class);

	public JedisUtil jedisUtil;

	public WeiXinGongZhongHaoPagePaser(JedisUtil jedisUtil) {
		this.jedisUtil = jedisUtil;
	}

	public List<Map<String, Object>> paserXmlPage(String html) {
		String temp = html.replaceAll("\\n{0,10}", "");
		temp = StringUtils.substringBetween(temp, "sogou.weixin.gzhcb(",
				")<!--STATUS");
		if (temp == null) {
			return null;
		}
		JSONObject jsonObject = JSONObject.fromObject(temp);
		Object jsonArray = jsonObject.get("items");
		JSONArray jsonArray2 = JSONArray.fromObject(jsonArray);
		List<Map<String, Object>> weixins = new ArrayList<Map<String, Object>>();
		for (Object object : jsonArray2) {
			Map<String, Object> map = parser((String) object);
			String md5 = MD5.GetMD5Code((String) map.get("url"));
			if (!jedisUtil.containsInSet("uniqurls", md5)) {
				jedisUtil.addSet("uniqurls", md5);
			} else {
				logger.info(map.get("url") + "已经存在");
				continue;
			}
			weixins.add(map);
		}
		return weixins;
	}

	public Map<String, Object> parser(String object) {
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
}
