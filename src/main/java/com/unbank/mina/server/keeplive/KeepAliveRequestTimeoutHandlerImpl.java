package com.unbank.mina.server.keeplive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;

public class KeepAliveRequestTimeoutHandlerImpl implements
		KeepAliveRequestTimeoutHandler {
	private static Log logger = LogFactory
			.getLog(KeepAliveRequestTimeoutHandlerImpl.class);

	public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession arg1)
			throws Exception {

		logger.info(arg1.getRemoteAddress() + "心跳监测超时");

	}

}
