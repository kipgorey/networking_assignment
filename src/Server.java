import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.JsonObject;

// The server class here is basically going to be equal to a 

public class Server {
	
	public static List<Datum> datumList;
	public static List<Datum> all_tours;
	public static List<String[]> csv_data; // a matrix of String data from the csv schedule file
	public static String file_name;
	public static String csv_name;
	// public static List<Trade> trades_objs;
	public static String agent_filename;
	public static String start_url = "https://us-west2-csci201-376723.cloudfunctions.net/events/";
	
	public static List<int[]> agent_info;
	
	private Util u = new Util();
	
	public long start_time;
	public int elapsed_time;
	
	
	private Vector<ClientHandler> serverThreads;
	
	
	public Server(int port)
	{
		try
		{
			int count = 0;
			int max_total = agent_info.size();
			System.out.println("Binding to port " + port);
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Bound to port " + port);
			serverThreads = new Vector<ClientHandler>();
			while(true) {
				Socket s = ss.accept(); // blocking
				System.out.println("Connection from: " + s.getInetAddress());
				ClientHandler st = new ClientHandler (s, this);
				serverThreads.add(st);
				count++;
				if(count < max_total)
				{
					broadcast(max_total - count + " more agent is needed before the service can begin");
					broadcast("waiting...");
				} else {
					broadcast("All agents have arrived");
					broadcast("Starting service");
					
					// need to divide the server information
					Util.setStart(System.currentTimeMillis());
					start_time = System.currentTimeMillis();
					// set the initial start time of buying and selling
					
					buy_and_sell();
				}
				
			}
			
			
		} catch(IOException ioe) {
			System.out.println("ioe in constructor: " + ioe.getMessage());
		}
		
		
		
	}


	public static void main(String[] args) throws IOException
	{
		
		// Here, I need to ask the user to input the information about the client
		// initialize the arrays lists so that it isn't null when I try to add information
		// trades_objs = new ArrayList<>();
		
		
		csv_data = new ArrayList<>();
		agent_info = new ArrayList<>();
		
		openCSV();
		getAgentInfo();
		
		datumList = new ArrayList<>();
		
		for(int i = 0; i < csv_data.size(); i++)
		{
			String event_id = csv_data.get(i)[1]; // this is the snippet at the end
			getWebInfo(event_id);
			
		}
		
		
		Tour tour = new Tour();
		tour.setData(datumList);
		all_tours = tour.getData();

		/* *** all the necessary information is not set/loaded *** */

		
		// System.out.println("This is a test for the server application!");
	
		// the accept method here waits until a client joins at the socket and blocks program execution until then
		
		
		
		// Create a loop to stall the execution of the server / program until enough clients join
		// Socket s;
		
		Server single_serv = new Server(3456);
		
		
		// s.close();
		
	}
	
	
	
	/* *** Method that I use to open the csv file *** */
	
	private static void openCSV()
	{
		
		System.out.println("What is the path of the schedule file?");
		Scanner scan = new Scanner(System.in);
		
		csv_name = scan.nextLine();
		
		File openfile = new File(csv_name);
		
		Scanner s = null;
		try {
			s = new Scanner(openfile);
		} catch (FileNotFoundException e) {
			// If the user fails to put in a valid filename, recurse the program and make them reinput the name
			openCSV();
			return;
		}
		

		
		while(s.hasNext())
		{
			String[] st = s.nextLine().split(",");
			csv_data.add(st);
			
		}
		
		
		s.close();
		
		// output response confirming proper data load
		System.out.println("The schedule file has been read properly");
		return;
		
	}
	
	/* *** Method im using to put agent information in a n by n array - in this case, always 2x2 *** */
	
	
	private static void getAgentInfo()
	{
		
		System.out.println("What is the path of the agents file?");
		Scanner scan = new Scanner(System.in);
		
		agent_filename = scan.nextLine();
		
		File openfile = new File(agent_filename);
		
		Scanner s = null;
		try {
			s = new Scanner(openfile);
		} catch (FileNotFoundException e) {
			// If the user fails to put in a valid filename, recurse the program and make them reinput the name
			getAgentInfo();
			return;
		}
		
		// s.useDelimiter(",");
		while(s.hasNext())
		{
			String[] st = s.nextLine().split(",");
			// convert st info from string --> int
			int[] convert_arr = new int[st.length];
			
			for(int i = 0; i < st.length; i++)
			{
				convert_arr[i] = Integer.parseInt(st[i]);
			}
			
			agent_info.add(convert_arr);
			
		}
		s.close();
		
		// output response confirming proper data load
		System.out.println("The agents file has been read properly");
		
		// testing to check its being read properly
		
		for(int i = 0; i < agent_info.size(); i++)
		{
			for(int j = 0; j < agent_info.get(i).length; j++)
			{
				System.out.print(agent_info.get(i)[j] + " ");
			}
			System.out.println();
			
			
		}
		
		return;
		
		}
	
	
	
	private static void getWebInfo(String event_id) throws IOException 
	{
		String complete_url = start_url+event_id;
		URL url = new URL(complete_url);
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            String page_info = response.toString();
            
            Gson gson = new Gson();
            Datum datum = gson.fromJson(page_info, Datum.class);
            datumList.add(datum);
                		
    		
    		System.out.print("OMS ");
            
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            // handle "404 Not Found" error
        	
            System.out.println("Event not found");
        } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // handle "401 Unauthorized" error
        	
        	
            System.out.println("Unauthorized access");
        } else {
            // handle other errors
            System.out.println("Error: " + responseCode);
        }
        
        
		
	}
	
	public void broadcast(String message) {
		if (message != null) {
			System.out.println(message);
			for(ClientHandler threads : serverThreads) {
					threads.sendMessage(message);
				
			}
		}
	}
	
	public void buy_and_sell() {
		boolean all_done = false;
		
		while(!all_done)
		{
			int count = 0;
			for(ClientHandler threads : serverThreads) {
				
				for(int i = 0; i < all_tours.size(); i++)
				{
					 if(Integer.parseInt(csv_data.get(i)[2]) * datumList.get(i).getPrice() < agent_info.get(count)[1])
					 { // this if statement is supposed to suggest that the agents balance is larger than that of the sale
						 elapsed_time = (int) ((System.currentTimeMillis() - start_time) / 1000);
						 while(elapsed_time < Integer.parseInt(csv_data.get(i)[0]))
						 {
							 threads.sleep();
							 elapsed_time = (int) ((System.currentTimeMillis() - start_time) / 1000);
						 }
						 if(Integer.parseInt(csv_data.get(i)[2]) > 0)
						 {
							 threads.sendMessage(u.getCurrentTime(System.currentTimeMillis()) + "Assigning " + Integer.parseInt(csv_data.get(i)[2]) + " tickets of " + datumList.get(i).getName() + " for purchase.");
							 agent_info.get(count)[1] -= Integer.parseInt(csv_data.get(i)[2]) * datumList.get(i).getPrice();
							 threads.sendMessage("Current balance: " + agent_info.get(count)[1]);
							 threads.sendMessage("" + elapsed_time);
						 } else {
							 threads.sendMessage(u.getCurrentTime(System.currentTimeMillis()) + "Assigning " + Integer.parseInt(csv_data.get(i)[2]) * -1 + " tickets of " + datumList.get(i).getName() + " for sale.");
							 agent_info.get(count)[1] -= Integer.parseInt(csv_data.get(i)[2]) * datumList.get(i).getPrice();
							 threads.sendMessage("Current balance: " + agent_info.get(count)[1]);
							 threads.sendMessage("" + elapsed_time);
						 }
					 }
				}
				 
				 count++;
			}
			
			all_done = true; // temporary
		
		}
	}
	
	
	
}

