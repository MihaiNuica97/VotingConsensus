import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Coordinator {
	private static int port;
	private static int loggerPort;
	private static int parts;
	private static ArrayList<String> options;
	private static int timeout;
	private static CoordinatorLogger logger;
	private static CoordinatorServer server;

	private void start(){

	}

	public static void main(String[] args) throws IOException {
		port =  Integer.parseInt(args[0]);
		loggerPort = Integer.parseInt(args[1]);
		parts = Integer.parseInt(args[2]);
		timeout = Integer.parseInt(args[3]);



		CoordinatorLogger.initLogger(loggerPort,port,timeout);
		logger = CoordinatorLogger.getLogger();
		server = new CoordinatorServer(port,parts,logger);






	}


}
