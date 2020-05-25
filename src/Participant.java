import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Participant {
	private static int cPort;
	private static int lPort;
	private static int port;
	private static int timeout;
	private static ParticipantLogger logger;
	private static Thread thread;
	private static HashMap<String,Integer> voteCounts;
	private static HashMap<Integer,Vote> votes;
	private static Connection serverConnection;
	private static ParticipantServer server;

	private static String getVoteOutcome(){
		voteCounts = new HashMap<String, Integer>();
		for(Vote vote : votes.values()){
			if(voteCounts.get(vote.getVote())==null){
				voteCounts.put(vote.getVote(),1);
			}else{
				voteCounts.put(vote.getVote(),(voteCounts.get(vote.getVote()))+1);
			}
		}
		ArrayList<String>voteRanks = new ArrayList<String> (voteCounts.keySet());
		String voteOutcome = "Z";
		int best = 0;
		for(String vote: voteRanks){
			int count = voteCounts.get(vote);
			if(count>best){
				best = count;
				voteOutcome = vote;
			}
			if(count == best){
				if(vote.compareToIgnoreCase(voteOutcome) < 0){
					voteOutcome = vote;
				}
			}
		}
		return voteOutcome;
	}

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
				int maxRounds = otherParts.size() +1;

				server = new ParticipantServer(port,otherParts,logger);


//				VOTE_OPTIONS
				ArrayList<String> voteOptions = new ArrayList(Arrays.asList(serverConnection.getInput()));
				voteOptions.remove(0);
				logger.voteOptionsReceived(voteOptions);
				Vote myVote = new Vote(port, voteOptions.get(new Random().nextInt(voteOptions.size())));

//				VOTE
				int round = 1;
				logger.beginRound(round);
				votes =  new HashMap<>();
				server.listen(votes, timeout);
//				if(port == 12346){
//					System.exit(0);
//				}
				String message = "VOTE " + port +" "+ myVote.getVote();
				for(int id: otherParts){
					ArrayList<Vote> list = new ArrayList<Vote>();
					list.add(myVote);
					logger.votesSent(id,list);
				}
				server.broadcast(message);
				while(server.handled < otherParts.size()){
					Thread.sleep(1000);
				}
				System.out.println(server.newVotes);
//				decide first outcome
				votes.put(port,myVote);
				String outcome = getVoteOutcome();
				if(!(myVote.getVote().equals(outcome))){
					myVote = new Vote(port,outcome);
					votes.put(port,myVote);
					server.newVotes.add(myVote);
				}
				logger.outcomeDecided(outcome,new ArrayList<Integer>(votes.keySet()));
				logger.endRound(round);
				round++;
				while(round <= maxRounds){
					logger.beginRound(round);
					votes =  new HashMap<>();
					server.listen(votes, timeout);
//				if(port == 12346){
//					System.exit(0);
//				}
					message = "VOTE " + port +" "+ myVote.getVote();
					server.broadcast(message);
					while(server.handled < otherParts.size()){
						Thread.sleep(1000);
					}
					System.out.println(server.newVotes);
//				decide first outcome
					votes.put(port,myVote);
					outcome = getVoteOutcome();
					if(!(myVote.getVote().equals(outcome))){
						myVote = new Vote(port,outcome);
						votes.put(port,myVote);
						server.newVotes.add(myVote);
					}
					logger.outcomeDecided(outcome,new ArrayList<Integer>(votes.keySet()));
					logger.endRound(round);
					round++;
				}
				String finalOutcome = "OUTCOME " + getVoteOutcome();
				for(int partPort: votes.keySet()){
					finalOutcome = finalOutcome + " "  + partPort;
				}
				serverConnection.send(finalOutcome);
				logger.outcomeNotified(getVoteOutcome(),new ArrayList<Integer>(votes.keySet()));
				System.exit(0);
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
//			System.exit(0);
		}).start();

	}
}
