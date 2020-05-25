import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ParticipantServer {
	private ServerSocket socket;
	private ArrayList<Integer> otherParts;
	HashMap<Integer,Connection> incoming;
	HashMap<Integer,Connection> outgoing;
	ArrayList<Vote> newVotes;
	int handled;
	int crashed;
	private static ParticipantLogger logger;
	private int port;


	private class IncomingHandler extends Thread{

		public void run(){
			logger.startedListening();
			while(incoming.size() < otherParts.size()){
				try {
					Socket newIncoming = socket.accept();
					Connection newConnection = new Connection(newIncoming);
					int incomingPort = Integer.parseInt(newConnection.getInput()[0]);
					logger.connectionAccepted(incomingPort);
					newConnection.port = incomingPort;
					incoming.put(incomingPort,newConnection);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class OutgoingHandler extends Thread{
		public  void run(){
			for(int outPort : otherParts){
				try {
					Socket newOutgoing = new Socket("localhost", outPort);
					logger.connectionEstablished(outPort);
					Connection newConnection = new Connection(newOutgoing);
					newConnection.port = outPort;
					newConnection.out.println(port);
					outgoing.put(newConnection.port,newConnection);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void listen(HashMap<Integer,Vote> votes, int timeout){
		newVotes = new ArrayList<Vote>();
		handled = 0;
		for(Connection connection: incoming.values()){
			new Thread(()->{
				try {
//					connection.socket.setSoTimeout(timeout);
					String message = connection.getInputRaw();
					logger.messageReceived(connection.port,message);
					parseVote(votes,message,connection.port);
					handled ++;
				} catch (Exception e) {
//					e.printStackTrace();
					logger.participantCrashed(connection.port);
					synchronized (this) {
						otherParts.remove((Integer) connection.port);
						incoming.remove(connection.port);
						outgoing.remove(connection.port);
					}
				}
			}).start();
		}
	}

	private synchronized void parseVote(HashMap<Integer,Vote> votes, String message,int from){
		ArrayList<String> tokens = new ArrayList(Arrays.asList(message.split(" ")));
		ArrayList<Vote> thisNewVotes = new ArrayList<>();
		tokens.remove(0);
		for(int i = 0; i<tokens.size();i = i+2 ){
			int partPort = Integer.parseInt(tokens.get(i));
			Vote newVote = new Vote(partPort, tokens.get(i + 1));
			if(votes.get(partPort)==null){
//				System.out.println("Null value for vote. Replacing...");
				votes.put(partPort, newVote);
				newVotes.add(newVote);
				thisNewVotes.add(newVote);
			}else{
				String existingVoteString = votes.get(partPort).getVote();
				if(!existingVoteString.equals(newVote.getVote())){
//					System.out.println("Different value for vote. Replacing...");
					votes.put(partPort, newVote);
					newVotes.add(newVote);
					thisNewVotes.add(newVote);
				}
			}
		}
		logger.votesReceived(from,thisNewVotes);
	}

	public void broadcast(String message){
		for(Connection connection : outgoing.values()){
			connection.send(message);
			logger.messageSent(connection.port,message);
		}
	}

	public ParticipantServer( int port, ArrayList<Integer> otherParts, ParticipantLogger logger) {
		this.otherParts = otherParts;
		this.port = port;
		this.logger = logger;
		crashed = 0;
		incoming = new HashMap<>();
		outgoing = new HashMap<>();
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

		IncomingHandler inHandler = new IncomingHandler();
		inHandler.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		OutgoingHandler outHandler = new OutgoingHandler();
		outHandler.start();

		while(incoming.size()<otherParts.size() && outgoing.size()<otherParts.size()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}


}
