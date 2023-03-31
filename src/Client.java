import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

	private BufferedReader br;
	private PrintWriter pw;
	public Client(String hostname, int port) {
		try {
			System.out.println("Trying to connect to " + hostname + ":" + port);
			Socket s = new Socket(hostname, port);
			System.out.println("Connected to " + hostname + ":" + port);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			pw = new PrintWriter(s.getOutputStream());
			this.start();
			
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient constructor: " + ioe.getMessage());
		}
	}
	public void run() {
		try {
			while(true) {
				String line = br.readLine();
	            if (line != null) {
	                System.out.println(line);
	            }
			}
		} catch (IOException ioe) {
			System.out.println("ioe in ChatClient.run(): " + ioe.getMessage());
		}
	}
	public static void main(String [] args) {
		Scanner s = new Scanner(System.in);
		System.out.println("Enter the server hostname");
		String server_name = s.next();
		System.out.println("Enter the server port number");
		int port_num = Integer.parseInt(s.next());
		Client cc = new Client(server_name, port_num);
	}
}

