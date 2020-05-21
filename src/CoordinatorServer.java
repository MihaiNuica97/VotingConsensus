import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class CoordinatorServer{
	private int port;
	private int parts;
	private CoordinatorLogger logger;
	private ServerSocket socket;
	private HashMap<Integer, Socket> connections;

	private class ClientHandler extends Thread{
		private ServerSocket socket;
		private HashMap<Integer, Socket> connections;
		private int parts;


		public ClientHandler(ServerSocket socket, HashMap<Integer, Socket> connections,int parts){
			this.socket = socket;
			this.connections = connections;
			this.parts = parts;
		}
		public void run(){
			Socket clientSocket;
			PrintWriter out;
			BufferedReader in;

			logger.startedListening(port);

			int connected = 0;
			while(connected < parts) {
				try {
					clientSocket = socket.accept();
					out = new PrintWriter(clientSocket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					String input = in.readLine();
					int clientPort = Integer.parseInt(input.split(" ")[1]);


					connections.put(clientPort, clientSocket);
					logger.joinReceived(clientPort);
					connected ++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("All Participants connected");
		}
	}

	public CoordinatorServer(int port, int parts, CoordinatorLogger logger) throws IOException {
		this.port = port;
		this.parts = parts;
		this.logger = logger;
		connections = new HashMap<Integer, Socket>();
		socket = new ServerSocket(port);

		ClientHandler handler = new ClientHandler(socket,connections,parts);
		handler.start();



	}
}
