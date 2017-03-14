package org.menacheri.jetserver.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChannelEchoServer {

	public static final int ECHO_PORT = 10007;

	public static void main(String[] args) {
		new ChannelEchoServer().run();
	}

	public void run() {
		ServerSocketChannel serverChannel = null;
		try {
			serverChannel = ServerSocketChannel.open();
			// serverChannel.configureBlocking(false);
			serverChannel.socket().bind(new InetSocketAddress(ECHO_PORT));
			System.out.println("ChannelEchoServerが起動しました(port=" + serverChannel.socket().getLocalPort() + ")");
			while (true) {
				SocketChannel channel = serverChannel.accept();
				// System.out.println(channel.socket().getRemoteSocketAddress() + ":[接続されました]");
				new Thread(new ChannelEchoThread(channel)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverChannel != null && serverChannel.isOpen()) {
				try {
					System.out.println("ChannelEchoServerを停止します。");
					serverChannel.close();
				} catch (IOException e) {
				}
			}
		}
	}

}

class ChannelEchoThread implements Runnable {

	private static final int BUF_SIZE = 1000;
	SocketChannel channel = null;

	public ChannelEchoThread(SocketChannel channel) {
		this.channel = channel;
	}

	public void run() {
		ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
		Charset charset = Charset.forName("UTF-8");
		String remoteAddress = channel.socket().getRemoteSocketAddress().toString();
		try {
			if (channel.read(buf) < 0) {
				return;
			}
			buf.flip();
			String input = charset.decode(buf).toString();
			// System.out.print(remoteAddress + ":" + input);
			buf.flip();
			channel.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			System.out.println(sdf.format(new Date()) + ":" + remoteAddress + ":[切断しました]");
			if (channel != null && channel.isOpen()) {
				try {
					channel.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	final Object res = context.handleNext(data);

	Object obj = context.getCurrentRequestObject();

	if (obj != null && obj instanceof HttpRequestWrapper) {
		String[] token = ((HttpRequestWrapper) obj).getParam(TokenUtil.KEY_HIDDEN_TOKEN);
		if (token != null && token.length > 0 && StringUtil.isNullOrEmpty(token[0])) {

		}
	}

}