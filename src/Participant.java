import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Participant {
	private static int cPort;
	private static int lPort;
	private static int port;
	private static int timeout;
	private static ParticipantLogger logger;
	private static Thread thread;

	private static Connection serverConnection;
	private static ParticipantServer server;


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
				serverConnection = new Connection(clientSocket);
				serverConnection.port = port;
				logger.connectionEstablished(cPort);
				logger.joinSent(cPort);
				serverConnection.out.println("JOIN " + port);
//				DETAILS
				ArrayList<Integer> otherParts = new ArrayList<>();
				String[] details = serverConnection.getInput();
				for (int i = 1; i<(details.length); i++){
					otherParts.add(Integer.parseInt(details[i]));
				}
				logger.detailsReceived(otherParts);

				server = new ParticipantServer(port,otherParts,logger);

			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.exit(0);
		}).start();

	}
}
