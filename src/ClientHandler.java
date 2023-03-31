import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
	
	private  BufferedReader d_in;
	private  PrintWriter d_out;
	private  Socket s;
	private Server server;
	
	// make constructor
	
	public ClientHandler(Socket sock, Server serv) throws IOException
	{
		
		s = sock;
		server = serv;
		d_in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		d_out = new PrintWriter(s.getOutputStream());
		this.start();
		
	}

	
	public void run()
	{
		
		
		while(true)
		{
			break;
		}
		
		

	}
	
	public void sendMessage(String message)
	{
		d_out.println(message);
		d_out.flush();
	}
	
	public void sleep()
	{
		try {
			this.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
