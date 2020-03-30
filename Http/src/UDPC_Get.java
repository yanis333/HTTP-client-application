
import static java.nio.channels.SelectionKey.OP_READ;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class UDPC_Get {

	private ArrayList<String> header;
	private String url ;
	private boolean Notverbiose;
	private String response;
	private InetAddress ip;
	private String option;
	private String status;
	private int counter;
	private String specialPath;
	private int routerPort;
	private String serverHost;
	private int serverPort;
	private SocketAddress routerAddress;
	private InetSocketAddress serverAddress;
	private int number;
	
	private String routerHost;
	
	public UDPC_Get(String[] args,int number) {
		this.number=number;
		counter=0;
		header = new ArrayList<String>();
		url ="";
		response= "";
		status="";
		ip= null;
		Notverbiose = true;
		option = null;
		
		if(args.length == 2) {
			try {
				ip = InetAddress.getByName(new URL(args[1])
		                .getHost()); 
				url = args[1];
			}catch(Exception e) {
				System.out.println("You did not enter a good Url");
				return;
			}
		}else {
			for(int x=0;x<args.length;x++) {
				 
				 if(args[x].equals("-o")) {
					 option = args[x+1]; 
				 }
				 if(args[x].equals("-v")) {
					 Notverbiose =false; 
				 }
				 
				 if(args[x].equals("-h") && (x+1)<args.length) {
					 if(!header.contains(args[x+1]+"\r\n")) {
						 header.add(args[x+1]+"\r\n");
					 }
				 }
				 
			 }
			if(args.length >=3 && option == null ){
				try {
					ip = InetAddress.getByName(new URL(args[args.length -1])
			                .getHost()); 
					url = args[args.length -1];
				}catch(Exception e) {
					System.out.println("You did not enter a good Url");
					return;
				}
		}else {
			try {
				ip = InetAddress.getByName(new URL(args[args.length -3])
		                .getHost()); 
				url = args[args.length -3];
			}catch(Exception e) {
				System.out.println("You did not enter a good Url");
				return;
			}
		}
			
		}
		
		for(int x=0;x<args.length;x++) {
			 
			 if(args[x].equals("-v")) {
				 Notverbiose =false; 
			 }
			 
			 if(args[x].equals("-h") && (x+1)<args.length) {
				 if(!header.contains(args[x+1]+"\r\n")) {
					 header.add(args[x+1]+"\r\n");
				 }
			 }
			 
		 }
		int counter =0;
		for(int x=0 ;x<this.url.length();x++) {
			if(this.url.charAt(x) == '/') {
				counter++;
			}
			if(counter ==3) {
				specialPath = this.url.substring(0,x);
				break;
			}
		}
		
	}
	
	public void sendRequest() throws IOException {
		 ip = InetAddress.getByName(new URL(url)
                .getHost()); 
		 
		 String headers ="";
		 for (String el : header) { 	
			 headers+=el;
	      }
		 
		 String request = "GET "+url+" HTTP/1.0\r\n"
		 		+ headers.toString() +"\r\n";
		 
		 try(DatagramChannel channel = DatagramChannel.open()){
			 handShakeWithServer(channel);
			 
			 long count =1;
			 boolean notFinish =true;
			 Packet packet = null;
			 int type = 5;

			 System.out.println("#"+this.number+"		Start sending request");
			 packet = sendRequestToServer(channel,request,3,count);
			 
			 while(packet == null || packet.getType() !=2) {
				 System.out.println("#"+this.number+"    Resending part of request #"+1);
				 packet = sendRequestToServer(channel,request,3,count);
			 }
			 
			 ArrayList<Packet> allPacket = new ArrayList<Packet>(10); 

			 System.out.println("#"+this.number+"    Start Getting data");
			 packet = sendRequestToServer(channel,"Get-Data",5,count);
			 
			 while(packet == null || packet.getType() !=4 ) {
				 packet = sendRequestToServer(channel,"Get-Data",5,count);
			 }
			 allPacket.add(0,packet);

			 System.out.println("#"+this.number+"    Getting Packets");
			 long sequence = packet.getSequenceNumber();
			 	packet = sendRequestToServer(channel,"AKN",2,packet.getSequenceNumber());
			 	while(notFinish) {
			 		if(packet == null) {

			 			System.out.println("#"+this.number+"    re asking for packets lost");
			 			packet = sendRequestToServer(channel,"AKN",2,sequence);
			 		}else if(packet.getType() == 5){
			 			System.out.println("#"+this.number+"    finished getting packets");
			 			notFinish = false;
			 		}else if(packet.getType() == 4) {
			 			try {
			 				Packet x = allPacket.get((int)packet.getSequenceNumber()-1);
			 				System.out.println("#"+this.number+"    Already received packet #"+packet.getSequenceNumber());

			 			}catch(Exception e) {
			 				allPacket.add((int)packet.getSequenceNumber()-1,packet);
			 				System.out.println("#"+this.number+"    New packet added to the list");
			 			}
			 			sequence =packet.getSequenceNumber();
			 			packet = sendRequestToServer(channel,"AKN",2,sequence);
			 		}
			 	}
			 	for(int x =0 ; x<allPacket.size();x++) {
			 		this.response+= new String(allPacket.get(x).getPayload(), StandardCharsets.UTF_8);
			 	}
			 
			 
		 }
		 
		 status = response.substring(9,10);
		 
		 
		if(status.equals("3") && counter !=5){
			 url = response.substring(response.indexOf("Location")+10,response.indexOf("Access-Control-Allow-Origin"));
			 url = url.replaceAll("\r\n","");
			 try {
				 ip = InetAddress.getByName(new URL(url)
			                .getHost()); 
			 }catch(Exception e){
				 url = specialPath+url;
			 }
			 counter++;
			 sendRequest();
		 }else if(counter ==5) {
			 System.out.println("Too many redirect calls, this is the final output!");
			 printResponse();
		 }else {
			 printResponse();
		 }
		 }
	private Packet sendRequestToServer(DatagramChannel channel, String request, int type,long sequenceNumber) throws IOException {
            Packet p = new Packet.Builder()
                    .setType(type)
                    .setSequenceNumber(sequenceNumber)
                    .setPortNumber(serverAddress.getPort())
                    .setPeerAddress(serverAddress.getAddress())
                    .setPayload(request.getBytes())
                    .create();
            channel.send(p.toBuffer(), routerAddress);
	 		 
			 channel.configureBlocking(false);
		     Selector selector = Selector.open();
		     channel.register(selector, OP_READ);
		     selector.select(5000);
		
		     Set<SelectionKey> keys = selector.selectedKeys();
		     if(keys.isEmpty()){
		     	System.out.println("No response after timeout");
		         return null;
		     }
		
		     ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
		     SocketAddress router = channel.receive(buf);
		     buf.flip();
		     Packet resp = Packet.fromBuffer(buf);
		     keys.clear();
		return resp;
		
	}

	private void setSettings() {
		OptionParser parser = new OptionParser();
        parser.accepts("router-host", "Router hostname")
                .withOptionalArg()
                .defaultsTo("localhost");

        parser.accepts("router-port", "Router port number")
                .withOptionalArg()
                .defaultsTo("3000");

        parser.accepts("server-host", "EchoServer hostname")
                .withOptionalArg()
                .defaultsTo("localhost");

        parser.accepts("server-port", "EchoServer listening port")
                .withOptionalArg()
                .defaultsTo("8007");

        OptionSet opts = parser.parse();

        // Router address
         routerHost = (String) opts.valueOf("router-host");
         routerPort = Integer.parseInt((String) opts.valueOf("router-port"));

        // Server address
        serverHost = (String) opts.valueOf("server-host");
        serverPort = Integer.parseInt((String) opts.valueOf("server-port"));

        routerAddress = new InetSocketAddress(routerHost, routerPort);
        serverAddress = new InetSocketAddress(serverHost, serverPort);
	}
	private void handShakeWithServer(DatagramChannel channel) throws IOException {
		Packet ans = null;
		setSettings();
		String msg= "Hi S";
		System.out.println("#"+this.number+"    "+msg);
		ans = sendRequestToServer(channel,msg,0,1);
		
		while(ans == null || ans.getType() != 1) {
			System.out.println("#"+this.number+"    "+msg);
			ans = sendRequestToServer(channel,msg,0,1);
		}
		
		System.out.println("#"+this.number+"		Answer from the server : "+new String(ans.getPayload(), StandardCharsets.UTF_8));
		
		msg= "AKN";
		System.out.println("#"+this.number+"    "+msg);
		ans = sendRequestToServer(channel,msg,2,1);
		
		while(ans == null) {
			System.out.println("#"+this.number+"    "+msg);
			ans = sendRequestToServer(channel,msg,2,1);
		}
	}

	public void printResponse() throws IOException {
		 if(Notverbiose) {
			 boolean verbioseFound =false;
			 String substringResponse = "";
			 Scanner scanner = new Scanner(response);
			 while (scanner.hasNextLine()) {
			   String line = scanner.nextLine();
			   if(line.equals("")) {
				   verbioseFound =true;
			   }
			  if(verbioseFound) {
				  substringResponse+= line+"\n";
			  }
			 }
			 scanner.close();
			 response = substringResponse;
		 }
		 
		 if(option !=null) {
			 writeToFileResponse();
		 }
		 else{
			 System.out.println();
			 System.out.println(response);
		 }
	}
	
	public void writeToFileResponse() throws IOException {
		try {
			FileWriter writer = new FileWriter(option, true);
	        writer.write(this.response);
	        writer.close();
        }catch(IOException e) {
        	System.out.println("wrong file format");
        	return;
        }
	}
}
