package com.unbank.spider.weixinbigV;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unbank.queue.InformationConsume;
import com.unbank.redis.JedisUtil;

@Component
public class WeiXinGongZhonghaoSpider {

	LinkedBlockingQueue<Object> informationQueue = new LinkedBlockingQueue<Object>();
//	@Autowired
//	JedisUtil jedisUtil;

	@PostConstruct
	public void init() {
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(new InformationConsume(informationQueue));
		executor.shutdown();
	}

	public void start() {
//		new WeiXinGongZhonghaoProducter(informationQueue, jedisUtil).create();

	}
}
