package TCPSimulation;
import java.net.*; 
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TCPServer {

	public static void main(String[] args) {
		
	ArrayList<String> originalPackets = new ArrayList<>();
	ArrayList<String> packetsShuffled = new ArrayList<>();
	populate(originalPackets);	
	shuffleOriginalArray(originalPackets, packetsShuffled);
	
	try (ServerSocket server = new ServerSocket(8080);
			Socket client = server.accept();
			PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			) {
		sendPackets(packetsShuffled, reader, writer);
		while(!packetsShuffled.isEmpty()) {
			missingIndexes(reader, packetsShuffled, originalPackets);
			sendPackets(packetsShuffled, reader, writer);
		}
	} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port 8080 or listening for a connection");
			System.exit(1);
	}
}
	
	public static void populate(ArrayList<String>packets) {

		packets.add("Hello 00");
		packets.add("client! 01");
		packets.add("You 02");
		packets.add("have 03");
		packets.add("just 04");
		packets.add("connected 05");
		packets.add("to 06");
		packets.add("the 07");
		packets.add("server. 08");
		packets.add("Here 09");
		packets.add("is 10");
		packets.add("your 11");
		packets.add("message 12");
		packets.add("sent 13");
		packets.add("via 14");
		packets.add("a 15");
		packets.add("TCP 16");
		packets.add("Protocol 17");
		packets.add("Simulation 18");
		packets.add("Program 19");
		
	}
	
	public static void shuffleOriginalArray(ArrayList<String> originalPackets, ArrayList<String> packetsShuffled) {
		
		//copy all packets and shuffle
		for(int i = 0; i< originalPackets.size(); i++) {
			packetsShuffled.add(originalPackets.get(i));
		}
		
		Collections.shuffle(packetsShuffled);
	}
	
	public static void sendPackets(ArrayList<String> packets, BufferedReader reader, PrintWriter writer ) {
		
		Random generator = new Random();
		int probability;
		
		try{

			for(int i = 0; i< packets.size(); i++) {
				
				probability = generator.nextInt(100) + 1; 
				
				if(probability >= 20) {
					//send packets with 80% probability
					System.out.println("Sent: " + packets.get(i));
					writer.println(packets.get(i));	
				}
			}
			//send undroppable packet with metadata of number of total packets
			writer.println("Finished 20");
			
		} catch (OutOfMemoryError e) {
			System.out.println(e.getMessage());
		}	
	}
	
	public static void missingIndexes(BufferedReader reader, ArrayList<String> packetsShuffled, ArrayList<String> originalPackets) {
		String stringMissingIndex;
		int missingIndex;
		
		//clear array list
		packetsShuffled.clear();
	
		try {
			
			stringMissingIndex = reader.readLine();

			if(!stringMissingIndex.equals("-1")) 
				System.out.println("Re-sending missing packets.");
			//if not all packets were received then listen to which are missing 
			//if all were received the client sends -1
			while (!stringMissingIndex.equals("-1")) {
				
					missingIndex = Integer.parseInt(stringMissingIndex);	
					packetsShuffled.add(originalPackets.get(missingIndex));
					//get the next missing index from the client
					stringMissingIndex = reader.readLine();
					
					Collections.shuffle(packetsShuffled);
				};
			} catch (IOException e) {
				System.exit(1);
		}
	}	
}
