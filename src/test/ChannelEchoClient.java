package org.menacheri.jetserver.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ChannelEchoClient extends Thread {
	private String sendStr;
	public static final int ECHO_PORT = 10007;
	public static final int BUF_SIZE = 1000;

	public static void main(String[] args) {
		ChannelEchoClient[] channelEchoClients = new ChannelEchoClient[1000];
		for (int i = 0; i < channelEchoClients.length ; i++) {
			channelEchoClients[i] = new ChannelEchoClient("test_" + i);
		}

		for (ChannelEchoClient tempChannelEchoClient : channelEchoClients) {
			tempChannelEchoClient.start();
		}
	}

	public ChannelEchoClient(String sendStr) {
		this.sendStr = sendStr;
	}

	public void run() {
		SocketChannel channel = null;
		ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
		Charset charset = Charset.forName("UTF-8");
		try {
			channel = SocketChannel.open(new InetSocketAddress("localhost", ECHO_PORT));
			// channel.configureBlocking(false);
			channel.write(charset.encode(CharBuffer.wrap(sendStr + "\n")));
			while (channel.isConnected()) {
				buf.clear();
				if (channel.read(buf) < 0) {
					return;
				}
				buf.flip();
				// System.out.print("受信：" + charset.decode(buf).toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (channel != null && channel.isOpen()) {
				try {
					channel.close();
				} catch (IOException e) {
				}
			}
		}
	}

}