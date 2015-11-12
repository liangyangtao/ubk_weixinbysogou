package com.unbank.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseQueue {
	public static Log logger = LogFactory.getLog(BaseQueue.class);

	public void put(LinkedBlockingQueue<Object> queue, Object object) {
		try {
			queue.put(object);
		} catch (InterruptedException e) {
			logger.info("", e);
		}
	}

	public Object take(LinkedBlockingQueue<Object> queue) {
		Object object = null;
		try {
			object = queue.take();
		} catch (InterruptedException e) {
			logger.info("", e);
		}
		return object;
	}

	public void sleeping(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
