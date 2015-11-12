package com.unbank.mina.server;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.unbank.Constants;

public class TLSServerHandler extends IoHandlerAdapter {

	static Log logger = LogFactory.getLog(TLSServerHandler.class);
	private static final Set<IoSession> sessions = Collections
			.synchronizedSet(new HashSet<IoSession>());

	public static Set<IoSession> checkSession = Collections
			.synchronizedSet(new HashSet<IoSession>());

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		InetSocketAddress remoteAddress = (InetSocketAddress) session
				.getRemoteAddress();
		logger.info(remoteAddress + "客户端到链接异常", cause);
		broadcast(remoteAddress + "客户端到链接异常" + cause.getMessage());
		// super.exceptionCaught(session, cause);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		sessions.remove(session);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		InetSocketAddress remoteAddress = (InetSocketAddress) session
				.getRemoteAddress();
		sessions.add(session);
		logger.info(remoteAddress + "与服务器建立连接");

	}

	// 当客户端发送的消息到达时:
	public void messageReceived(IoSession session, Object message) {
		InetSocketAddress remoteAddress = (InetSocketAddress) session
				.getRemoteAddress();
		logger.info("接收到客户端" + remoteAddress + "消息" + message);
		if (message instanceof Integer) {

		} else if (message instanceof String) {
			String temp = (String) message;
			if (temp.equals(Constants.HEARTBEATREQUEST)
					|| temp.equals(Constants.HEARTBEATRESPONSE)) {
				logger.info(remoteAddress + "心跳监测");
			} else {
				broadcast(remoteAddress + "异常消息" + temp);
			}
		}
	}

	public void messageSent(IoSession session, Object message) {

	};

	public void broadcast(String message) {
		synchronized (sessions) {
			for (IoSession session : sessions) {
				if (session.isConnected()) {
					session.write(message);
				}
			}
		}
	}
}