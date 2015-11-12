package com.unbank.spider.weixinbigV;

import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.unbank.redis.JedisUtil;

public class WeiXinGongZhonghaoProducter {
	public LinkedBlockingQueue<Object> informationQueue;
	public JedisUtil jedisUtil;
	private static Log logger = LogFactory
			.getLog(WeiXinGongZhonghaoProducter.class);

	public WeiXinGongZhonghaoProducter(
			LinkedBlockingQueue<Object> informationQueue, JedisUtil jedisUtil) {
		this.informationQueue = informationQueue;
		this.jedisUtil = jedisUtil;
	}

	public void create() {
		List<String> openids = new WeixinGongZhonghaoReader()
				.readGongZhongHaoList();
		for (String openid : openids) {
			openid = openid.split("openid=")[1];
			try {
				int totalPages = 1;
				for (int i = 1; i <= totalPages; i++) {
					try {
						String page = i + "";
						String html = new WeixinGongZhonghaoListPaser()
								.getWinxinEntity(openid, page);
						if (html == null) {
							logger.info(openid + "   的第" + i + "页没有数据");
						} else {
							if (html.contains("请输入验证码")) {
								logger.info("被屏蔽.....休息20分钟");
								Thread.sleep(20 * 60 * 1000);
								continue;
							}
						}
						List<Map<String, Object>> weixins = new WeiXinGongZhongHaoPagePaser(
								jedisUtil).paserXmlPage(html);
						if (weixins == null || weixins.size() == 0) {
							break;
						}
						fillInformationQueue(weixins);
						// "totalPages":10,"page"
						if (totalPages == 1) {
							try {
								String temp = html.replaceAll("\\n{0,10}", "");
								String s = StringUtils.substringBetween(temp,
										"totalPages\":", ",\"page");
								System.out.println("totalPages       " + s);
								totalPages = Integer.parseInt(s);
							} catch (Exception e) {
								logger.info("获取微信公众号", e);
								System.out.println(html);
								break;
							}
						}

					} catch (Exception e) {
						logger.info("采集公众号列表出错了", e);
						break;
					} finally {
						Thread.sleep(60000);
					}
				}
			} catch (Exception e) {
				logger.info("采集公众号列表出错了", e);
				continue;
			}

		}

	}

	public void fillInformationQueue(List<Map<String, Object>> weixins) {
		for (Map<String, Object> weixin : weixins) {
			informationQueue.add(weixin);
		}

	}

}
