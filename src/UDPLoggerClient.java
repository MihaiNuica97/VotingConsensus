import java.io.IOException;
import java.net.*;

public class UDPLoggerClient {
	
	private final int loggerServerPort;
	private final int processId;
	private final int timeout;

	/**
	 * @param loggerServerPort the UDP port where the Logger process is listening o
	 * @param processId the ID of the Participant/Coordinator, i.e. the TCP port where the Participant/Coordinator is listening on
	 * @param timeout the timeout in milliseconds for this process 
	 */
	public UDPLoggerClient(int loggerServerPort, int processId, int timeout) {
		this.loggerServerPort = loggerServerPort;
		this.processId = processId;
		this.timeout = timeout;
	}
	
	public int getLoggerServerPort() {
		return loggerServerPort;
	}

	public int getProcessId() {
		return processId;
	}
	
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Sends a log message to the Logger process
	 * 
	 * @param message the log message
	 * @throws IOException
	 */
	public void logToServer(String message) throws IOException {
		message = processId + " " + message;
//		System.out.println(message);
		byte[] buffer = message.getBytes();
		int tries = 3;
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(buffer,buffer.length, InetAddress.getLocalHost(),loggerServerPort);
		socket.send(packet);

		DatagramPacket newPacket = new DatagramPacket(buffer,buffer.length);
		String received;
		while(tries > 0) {
			try {
				socket.setSoTimeout(timeout);
				socket.receive(newPacket);
				received = new String(newPacket.getData(), newPacket.getOffset(), newPacket.getLength());
				if(received.equals("ACK")){
					break;
				}
			}catch (SocketException | SocketTimeoutException e) {
//				e.printStackTrace();
				tries--;
			}
		}
		socket.close();
	}
}
