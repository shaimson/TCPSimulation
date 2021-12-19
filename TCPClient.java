package TCPSimulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	
    public static void main(String[] args) throws IOException {	
    //initialize to 100 since don't know how many packets to expect
    String[] packets = new String[100];
    int counter = -1; // counter of amount of missing packets

    try (
        Socket client = new Socket("127.0.0.1", 8080);
        PrintWriter writer = // stream to write text requests to server
            new PrintWriter(client.getOutputStream(), true);
        BufferedReader reader= // stream to read text response from server
            new BufferedReader(new InputStreamReader(client.getInputStream())); 

    ) {
		String serverPacket;
		int qtyPackets = 0;
		
		serverPacket = reader.readLine();
		//finished completely is the last packet sent when the client stops requesting missing packets.
		//this indicates all packets have been received
		while (counter != 0) {
			//finished 20 is the  packet with metadata of number of total packets
			while (!serverPacket.equals("Finished 20")) {
			
			int packetIndex = Integer.parseInt(serverPacket.substring(serverPacket.length()-2));
			String packetData= serverPacket.substring(0, serverPacket.length()-3);
			
			packets[packetIndex] = packetData;
			
			System.out.println("Received: " + packets[packetIndex]);
			
			serverPacket = reader.readLine();     
		}
		
		//set quantity of packets to the metadata of the last received packet
		qtyPackets = Integer.parseInt(serverPacket.substring(serverPacket.length()-2));
		
		counter = 0;
		for (int i = 0; i < qtyPackets; i++) {
			
			if (packets[i] == null) {
				//send server index of missing packets
				writer.println(i);
				System.out.println("Missing: " + i);
				counter++;
			}
		}	
		
		//Packet to indicate that all packets have been received 
		writer.println("-1");
		
		//breaks when 0 missing packets
		if (counter == 0) {
			break;
		}
		
		serverPacket = reader.readLine();  
	}
		
		//display the message
		System.out.println("This is the message:");
		for(int i = 0; i< qtyPackets; i++) {
			System.out.print(packets[i] + " ");
		}
			
    } catch (UnknownHostException e) {
        System.err.println("Don't know about host 127.0.0.1");
        System.exit(1);
    } catch (IOException e) {
        System.err.println("Couldn't get I/O for the connection to 127.0.0.1");
        System.exit(1);
    } 
    
}

	


}
