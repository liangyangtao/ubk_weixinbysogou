package com.unbank.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartWeiXinGongZhonghaoSpider {
	private static Log logger = LogFactory
			.getLog(StartWeiXinGongZhonghaoSpider.class);

	@Autowired
	SougouSpiderByChrome sougouSpiderByChrome;

	public void executeInternal() {
		try {
			sougouSpiderByChrome.spider();
		} catch (Exception e) {
			logger.info("启动采集微信公众号定时任务出错", e);
		}
	}
}
