package com.unbank.mina.clent.keeplive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

import com.unbank.Constants;

public class ClentKeepAliveMessageFactoryImpl implements
		KeepAliveMessageFactory {

	private static Log logger = LogFactory
			.getLog(ClentKeepAliveMessageFactoryImpl.class);

	public Object getRequest(IoSession arg0) {
		return Constants.HEARTBEATREQUEST;
	}

	public Object getResponse(IoSession arg0, Object arg1) {
		return Constants.HEARTBEATRESPONSE;
	}

	public boolean isRequest(IoSession arg0, Object arg1) {
//		logger.info(arg1);
		if (arg1 instanceof String) {
			String temp = (String) arg1;
			return temp.equals(Constants.HEARTBEATREQUEST);
		}
		return false;
	}

	public boolean isResponse(IoSession arg0, Object message) {
//		logger.info(message);
		if (message instanceof String) {
			String temp = (String) message;

			return temp.equals(Constants.HEARTBEATRESPONSE);
		}
		return false;
	}
}
