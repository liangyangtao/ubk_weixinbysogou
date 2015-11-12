package com.unbank.mina.clent;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.unbank.Constants;
import com.unbank.mina.clent.keeplive.ClentKeepAliveMessageFactoryImpl;
import com.unbank.mina.clent.keeplive.KeepAliveRequestTimeoutHandlerImpl;
import com.unbank.mina.clent.listener.IoListener;

public class TLSClinet {
	public static TLSClinet tlsClinet;
	public static Log logger = LogFactory.getLog(TLSClinet.class);
	public NioSocketConnector connector;
	public IoSession session;
	public ConnectFuture future;

	// public static TLSClinet getInstance() {
	// if (tlsClinet == null) {
	// tlsClinet = new TLSClinet();
	// }
	// return tlsClinet;
	//
	// }

	public TLSClinet() {
		init();
	}

	public void init() {
		fillConnector();
	}

	private boolean fillConnector() {
		try {
			connector = new NioSocketConnector();
			connector.setConnectTimeoutMillis(Constants.CLENT_TIMEOUT);
			// 创建接受数据的过滤器
			DefaultIoFilterChainBuilder chain = connector.getFilterChain();
			// 设定这个过滤器将一行一行(/r/n)的读取数据
			chain.addLast("myChin", new ProtocolCodecFilter(
					new TextLineCodecFactory()));
			TextLineCodecFactory factory = new TextLineCodecFactory(
					Charset.forName("UTF-8"));
			factory.setDecoderMaxLineLength(Integer.MAX_VALUE);// 设定后服务器可以接收大数据
			factory.setEncoderMaxLineLength(Integer.MAX_VALUE);
			// 设置编码过滤器和按行读取数据模式
			connector.getFilterChain().addLast("codeobj",
					new ProtocolCodecFilter(factory));
			// 心跳监测
			KeepAliveMessageFactory keepAliveMessageFactory = new ClentKeepAliveMessageFactoryImpl();
			KeepAliveRequestTimeoutHandler keepAliveRequestTimeoutHandler = new KeepAliveRequestTimeoutHandlerImpl();
			KeepAliveFilter keepAliveFilter = new KeepAliveFilter(
					keepAliveMessageFactory, IdleStatus.BOTH_IDLE,
					keepAliveRequestTimeoutHandler);
			// ** 是否回发 *//*
			keepAliveFilter.setForwardEvent(true);
			keepAliveFilter.setRequestTimeout(Constants.HEART_TIMEOUT);
			// ** 发送频率 *//*
			keepAliveFilter.setRequestInterval(Constants.HEARTBEATRATE);
			connector.getFilterChain().addLast("heartbeat", keepAliveFilter);
			connector.setHandler(new TLSClentHandler());
			connector.setDefaultRemoteAddress(new InetSocketAddress(
					Constants.SERVERIP, Constants.SERVERPORT));
			// 连接到服务器：
			connector.addListener(new IoListener() {
				@Override
				public void sessionDestroyed(IoSession arg0) throws Exception {
					while (true) {
						try {
							Thread.sleep(30000);
							future = connector.connect();
							future.awaitUninterruptibly();// 等待连接创建成功
							session = future.getSession();// 获取会话
							if (session.isConnected()) {
								logger.info("断线重连["
										+ connector.getDefaultRemoteAddress()
												.getHostName()
										+ ":"
										+ connector.getDefaultRemoteAddress()
												.getPort() + "]成功");
								break;
							}
						} catch (Exception ex) {
							logger.info("重连服务器登录失败,30秒再连接一次", ex);
						}
					}
				}
			});

			while (true) {
				try {
					future = connector.connect();
					future.awaitUninterruptibly(); // 等待连接创建成功
					session = future.getSession(); // 获取会话
					break;
				} catch (RuntimeIoException e) {
					logger.info("连接服务器失败,30秒再连接一次", e);
					Thread.sleep(30000);// 连接失败后,重连间隔5s
				}
			}
		} catch (Exception e) {
			logger.info("链接服务器失败,30秒后重试", e);
			return false;
		}
		return true;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public ConnectFuture getFuture() {
		return future;
	}

	public void setFuture(ConnectFuture future) {
		this.future = future;
	}

}
