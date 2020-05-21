import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class UDPLoggerServer {
	private static int port;
	private static Thread loggerThread;

	private static void listen(int port) throws IOException {
		DatagramSocket socket = new DatagramSocket(port);
		byte[] buffer = new byte[400];
		System.out.println("Listening on port " + port);
		String ack = "ACK";
		FileOutputStream output = new FileOutputStream("logfile.txt");

		while(true){
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			socket.receive(packet);
			InetAddress address = packet.getAddress();
			int clientPort = packet.getPort();
			String received = new String(packet.getData(), packet.getOffset(), packet.getLength());

			String log = clientPort +" "+ System.currentTimeMillis() +" " + received+ System.getProperty("line.separator");
			System.out.println(log);
			output.write(log.getBytes());


			byte[] newBuffer = ack.getBytes();
			packet = new DatagramPacket(newBuffer, newBuffer.length, address, clientPort);
			socket.send(packet);
		}
	}





	public static void main(String[] args) {
		port =  Integer.parseInt(args[0]);
		loggerThread = new Thread(() ->{
			try {
				listen(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		loggerThread.start();
	}
}
