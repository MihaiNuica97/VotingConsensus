import java.io.IOException;
import java.util.ArrayList;

public class Coordinator {
	private static int port;
	private static int loggerPort;
	private static int parts;
	private static ArrayList<String> options;
	private static int timeout;
	private static CoordinatorLogger logger;


	public static void main(String[] args) throws IOException {
		port =  Integer.parseInt(args[0]);
		loggerPort = Integer.parseInt(args[1]);
		parts = Integer.parseInt(args[2]);
		timeout = Integer.parseInt(args[3]);

		CoordinatorLogger.initLogger(loggerPort,port,500);
		logger = CoordinatorLogger.getLogger();

		logger.startedListening(port);


	}


}
