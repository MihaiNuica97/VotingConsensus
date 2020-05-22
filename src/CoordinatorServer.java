import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class CoordinatorServer{
	private int port;
	private int parts;
	private CoordinatorLogger logger;
	private ServerSocket socket;
	private ArrayList<Connection> connections;

	private class ClientHandler extends Thread{
		private ServerSocket socket;
		private ArrayList<Connection> connections;
		private int parts;


		public ClientHandler(ServerSocket socket, ArrayList<Connection> connections,int parts){
			this.socket = socket;
			this.connections = connections;
			this.parts = parts;
		}
		public void run(){
			Socket clientSocket;
			PrintWriter out;
			BufferedReader in;

			logger.startedListening(port);

			// JOIN phase

			while(connections.size() < parts) {
				try {
					clientSocket = socket.accept();
					Connection connection = new Connection(clientSocket);

					String[] input = connection.in.readLine().split(" ");
					switch (input[0]){
						case "JOIN":
							connections.add(connection);
							connection.port = Integer.parseInt(input[1]);
							logger.joinReceived(connection.port);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

//			DETAILS phase
			for(Connection connection : connections){
				String details = "DETAILS";
				ArrayList detailsList = new ArrayList<Integer>();
				for(Connection connection1: connections){
					if(connection1.port != connection.port){
						details = details + " " + connection1.port;
						detailsList.add(connection1.port);
					}
				}
				logger.detailsSent(connection.port,detailsList);
				connection.out.println(details);
			}
		}
	}

	public CoordinatorServer(int port, int parts, CoordinatorLogger logger) throws IOException {
		this.port = port;
		this.parts = parts;
		this.logger = logger;
		connections = new ArrayList<Connection>();
		socket = new ServerSocket(port);

		ClientHandler handler = new ClientHandler(socket,connections,parts);
		handler.start();



	}
}
