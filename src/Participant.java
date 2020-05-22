import sun.security.x509.IPAddressName;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Participant {
	private static int cPort;
	private static int lPort;
	private static int port;
	private static int timeout;
	private static ParticipantLogger logger;
	private static Thread thread;

	private static Connection connection;



	public static void main(String[] args) throws IOException {

		cPort =  Integer.parseInt(args[0]);
		lPort = Integer.parseInt(args[1]);
		port = Integer.parseInt(args[2]);
		timeout = Integer.parseInt(args[3]);
		ParticipantLogger.initLogger(lPort,port,timeout);
		logger = ParticipantLogger.getLogger();


		new Thread(()->{
			try {
//				JOIN
				Socket clientSocket = new Socket("localhost", cPort);
				connection = new Connection(clientSocket);
				connection.port = port;
				logger.startedListening();
				logger.joinSent(cPort);
				connection.out.println("JOIN " + port);
//				DETAILS
				ArrayList<Integer> otherParts = new ArrayList<>();
				String[] details = connection.getInput();
				for (int i = 1; i<(details.length); i++){
					otherParts.add(Integer.parseInt(details[i]));
				}
				logger.detailsReceived(otherParts);
				System.out.println(otherParts);
//				ServerSocket serverSocket = new ServerSocket(port);
//				Socket newClientSocket = serverSocket.accept();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

	}
}
