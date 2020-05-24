import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Participant {
	private static int cPort;
	private static int lPort;
	private static int port;
	private static int timeout;
	private static ParticipantLogger logger;
	private static Thread thread;
	private static Vote vote;

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
				serverConnection.port = cPort;
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


//				VOTE_OPTIONS
				ArrayList<String> voteOptions = new ArrayList(Arrays.asList(serverConnection.getInput()));
				voteOptions.remove(0);
				logger.voteOptionsReceived(voteOptions);
				vote = new Vote(port, voteOptions.get(new Random().nextInt(voteOptions.size())));
				System.out.println(vote.toString());


			} catch (IOException e) {
				e.printStackTrace();
			}
//			System.exit(0);
		}).start();

	}
}
