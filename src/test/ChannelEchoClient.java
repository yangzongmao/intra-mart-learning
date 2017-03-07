package org.menacheri.jetserver.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ChannelEchoClient extends Thread {
	private String sendStr;
	public static final int ECHO_PORT = 18090;
	public static final int BUF_SIZE = 1000;

	public static void main(String[] args) {
		ChannelEchoClient[] channelEchoClients = new ChannelEchoClient[2000];
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
			byte[] bytes = new byte[] {(byte) 00, (byte) 51, (byte) 8, (byte) 1, (byte) 0, (byte) 4, (byte) 117, (byte) 115, (byte) 101, (byte) 114, (byte) 0, (byte) 4, (byte) 112, (byte) 97, (byte) 115, (byte) 115, (byte) 0, (byte) 23, (byte) 90, (byte) 111, (byte) 109, (byte) 98, (byte) 105, (byte) 101, (byte) 95, (byte) 82, (byte) 79, (byte) 79, (byte) 77, (byte) 95, (byte) 49, (byte) 95, (byte) 82, (byte) 69, (byte) 70, (byte) 95, (byte) 75, (byte) 69, (byte) 89, (byte) 95, (byte) 49, (byte) 0, (byte) 6, (byte) 80, (byte) 67, (byte) 49, (byte) 50, (byte) 55, (byte) 54, (byte) 0, (byte) 0, (byte) 29, (byte) 56};
			channel.write(charset.encode(CharBuffer.wrap(new String(bytes) + "\n")));
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