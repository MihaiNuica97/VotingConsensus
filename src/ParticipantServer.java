import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ParticipantServer {
	private ServerSocket socket;
	private ArrayList<Integer> otherParts;
	HashMap<Integer,Connection> incoming;
	HashMap<Integer,Connection> outgoing;
	private static ParticipantLogger logger;
	private int port;


	private class IncomingHandler extends Thread{


		public IncomingHandler(){

		}

		public void run(){

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

	public ParticipantServer( int port, ArrayList<Integer> otherParts, ParticipantLogger logger) {
		this.otherParts = otherParts;
		this.port = port;
		this.logger = logger;
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
		System.out.print("Part "+ port + " has established all connections");


	}

}
