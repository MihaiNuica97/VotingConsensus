import sun.security.x509.IPAddressName;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Participant {
	private static int cPort;
	private static int lPort;
	private static int port;
	private static int timeout;
	private static ParticipantLogger logger;
	private static Thread thread;
	private static Socket clientSocket;




	public static void main(String[] args) throws IOException {

		cPort =  Integer.parseInt(args[0]);
		lPort = Integer.parseInt(args[1]);
		port = Integer.parseInt(args[2]);
		timeout = Integer.parseInt(args[3]);
		ParticipantLogger.initLogger(lPort,port,timeout);
		logger = ParticipantLogger.getLogger();


		thread = new Thread(()->{
			PrintWriter out;
			BufferedReader in;
			try {
				clientSocket = new Socket(InetAddress.getLocalHost(), cPort);
				out = new PrintWriter(clientSocket.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				out.println("JOIN " + port);
			} catch (IOException e) {
				e.printStackTrace();
			}
			logger.startedListening();
		});
		thread.start();

	}
}
