package com.unbank;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Spider {
	private static Log logger = LogFactory.getLog(Spider.class);
	static {
		// 启动日志
		try {
			PropertyConfigurator.configure(Spider.class.getClassLoader()
					.getResource("").toURI().getPath()
					+ "log4j.properties");
			logger.info("---日志系统启动成功---");
		} catch (Exception e) {
			logger.error("日志系统启动失败:", e);
		}
	}

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext(
				new String[] { "applicationContext.xml" });
	}
}
