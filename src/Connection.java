import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
	int port;
	Socket socket;
	PrintWriter out;
	BufferedReader in;


	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public String[] getInput() throws IOException {
		return in.readLine().split(" ");
	}

	public String getInputRaw() throws  IOException{
		return in.readLine();
	}
	public void send(String message){
		out.println(message);
		out.flush();
	}
}