
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
	
	private String routerHost;
	
	public UDPC_Get(String[] args) {
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
			 
			 Packet packet = sendRequestToServer(channel,request,3,1);
			 while(packet.getType() != 5) {
				 this.response+= new String(packet.getPayload(), StandardCharsets.UTF_8);
			  packet = sendRequestToServer(channel,"AKN-Data",2,packet.getSequenceNumber());
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
		setSettings();
            String msg = "Hi S";
            Packet p = new Packet.Builder()
                    .setType(0)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddress.getPort())
                    .setPeerAddress(serverAddress.getAddress())
                    .setPayload(msg.getBytes())
                    .create();
            channel.send(p.toBuffer(), routerAddress);

            //logger.info("Sending \"{}\" to router at {}", msg, routerAddr);

            // Try to receive a packet within timeout.
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, OP_READ);
            System.out.println("Sending SYN to server!");
            System.out.println("Waiting for the response!");
            //logger.info("Waiting for the response");
            selector.select(5000);

            Set<SelectionKey> keys = selector.selectedKeys();
            if(keys.isEmpty()){
            	System.out.println("No response after timeout");
                //logger.error("No response after timeout");
                return;
            }

            // We just want a single response.
            ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LEN);
            SocketAddress router = channel.receive(buf);
            buf.flip();
            Packet resp = Packet.fromBuffer(buf);
            String payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
            keys.clear();
            if(resp.getType() == 1) {
            	System.out.println("Answer from the server : "+payload);
            
            msg = "AKN";
            p = new Packet.Builder()
                    .setType(2)
                    .setSequenceNumber(1L)
                    .setPortNumber(serverAddress.getPort())
                    .setPeerAddress(serverAddress.getAddress())
                    .setPayload(msg.getBytes())
                    .create();
            channel.send(p.toBuffer(), routerAddress);
            channel.configureBlocking(false);
            selector = Selector.open();
            channel.register(selector, OP_READ);
            selector.select(5000);

            keys = selector.selectedKeys();
            if(keys.isEmpty()){
            	System.out.println("No response after timeout");
                //logger.error("No response after timeout");
                return;
            }

            // We just want a single response.
            buf = ByteBuffer.allocate(Packet.MAX_LEN);
            router = channel.receive(buf);
            buf.flip();
            resp = Packet.fromBuffer(buf);
            payload = new String(resp.getPayload(), StandardCharsets.UTF_8);
            keys.clear();
        }else {
        	System.out.println("ERROR IN THE SERVER");
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
