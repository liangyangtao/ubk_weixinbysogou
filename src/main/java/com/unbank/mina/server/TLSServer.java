package com.unbank.mina.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.unbank.Constants;
import com.unbank.mina.server.keeplive.KeepAliveMessageFactoryImpl;
import com.unbank.mina.server.keeplive.KeepAliveRequestTimeoutHandlerImpl;
import com.unbank.mina.ssl.BogusSslContextFactory;

public class TLSServer {

	private static Log logger = LogFactory.getLog(TLSServer.class);

	public TLSServer() {
		createAcceptor();
	}

	private static void createAcceptor() {
		try {
			// 创建服务器端连接器
			SocketAcceptor acceptor = new NioSocketAcceptor();
			acceptor.getSessionConfig().setReadBufferSize(2048);
			acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
			// acceptor.setReuseAddress(true);
			// 获取默认过滤器
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			// 设置加密过滤器
			if (Constants.USE_SSL) {
				addSSLSupport(chain);
			}
			// 设置编码过滤器和按行读取数据模式
			// chain.addLast("codec", new ProtocolCodecFilter(
			// new TextLineCodecFactory(Charset.forName("UTF-8"))));
			chain.addLast("threadPool",
					new ExecutorFilter(Executors.newCachedThreadPool()));
			// 设定 对象传输工厂
			// ObjectSerializationCodecFactory factory = new
			// ObjectSerializationCodecFactory();
			// 设定传输最大值
			TextLineCodecFactory factory = new TextLineCodecFactory(
					Charset.forName("UTF-8"));
			factory.setDecoderMaxLineLength(Integer.MAX_VALUE);// 设定后服务器可以接收大数据
			factory.setEncoderMaxLineLength(Integer.MAX_VALUE);

			// 设置编码过滤器和按行读取数据模式
			chain.addLast("codeobj", new ProtocolCodecFilter(factory));

			KeepAliveMessageFactory keepAliveMessageFactory = new KeepAliveMessageFactoryImpl();
			KeepAliveRequestTimeoutHandler keepAliveRequestTimeoutHandler = new KeepAliveRequestTimeoutHandlerImpl();
			KeepAliveFilter keepAliveFilter = new KeepAliveFilter(
					keepAliveMessageFactory, IdleStatus.BOTH_IDLE,
					keepAliveRequestTimeoutHandler);
			// ** 是否回发 *//*
			keepAliveFilter.setForwardEvent(true);
			keepAliveFilter.setRequestTimeout(Constants.HEART_TIMEOUT);
			// ** 发送频率 *//*
			keepAliveFilter.setRequestInterval(Constants.HEARTBEATRATE);
			// acceptor.getFilterChain().addLast("heartbeat", keepAliveFilter);

			// 设置事件处理器
			acceptor.setHandler(new TLSServerHandler());
			// 处理器的代码如下
			// 服务绑定到此端口号
			acceptor.bind(new InetSocketAddress(Constants.SERVERPORT));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addSSLSupport(DefaultIoFilterChainBuilder chain)
			throws Exception {
		SslFilter sslFilter = new SslFilter(
				BogusSslContextFactory.getInstance(true));
		chain.addLast("sslFilter", sslFilter);
	}
}