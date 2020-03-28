import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;

public class UDPS {

    private static final Logger logger = LoggerFactory.getLogger(UDPServer.class);
    private Map<InetAddress, Map<Integer,ArrayList<Packet>>> clientRegister = new HashMap<InetAddress, Map<Integer,ArrayList<Packet>>>();
    private String bodyInfo = "";
    private void listenAndServe(int port) throws IOException {

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(port));
            logger.info("EchoServer is listening at {}", channel.getLocalAddress());
            ByteBuffer buf = ByteBuffer
                    .allocate(Packet.MAX_LEN)
                    .order(ByteOrder.BIG_ENDIAN);

            for (; ; ) {
                buf.clear();
                SocketAddress router = channel.receive(buf);

                // Parse a packet from the received raw data.
                buf.flip();
                Packet packet = Packet.fromBuffer(buf);
                buf.flip();

                String payload = new String(packet.getPayload(), UTF_8);
                logger.info("Packet: {}", packet);
                logger.info("Payload: {}", payload);
                logger.info("Router: {}", router);

                // Send the response to the router not the client.
                // The peer address of the packet is the address of the client already.
                // We can use toBuilder to copy properties of the current packet.
                // This demonstrate how to create a new packet from an existing packet.
                Packet resp = null;
                switch(packet.getType()) {
                case 0:
                	resp = respondHandShake(packet);
                	channel.send(resp.toBuffer(), router);
                	break;
                case 2:
                	if(clientRegister.containsKey(packet.getPeerAddress())) {
                		if(clientRegister.get(packet.getPeerAddress()).containsKey(packet.getPeerPort())) {
                			if(packet.getSequenceNumber() == clientRegister.get(packet.getPeerAddress()).get(packet.getPeerPort()).size()) {
                				resp = packet.toBuilder()
                		         		.setType(5)
                		                 .setPayload("DONE".getBytes())
                		                 .create();
                				
                				channel.send(resp.toBuffer(), router);
                				}else if(packet.getSequenceNumber() < clientRegister.get(packet.getPeerAddress()).get(packet.getPeerPort()).size()){
                					resp = clientRegister.get(packet.getPeerAddress()).get(packet.getPeerPort()).get((int)packet.getSequenceNumber());
                					channel.send(resp.toBuffer(), router);
                				}
                			}else {
                				ArrayList<Packet> p = new ArrayList<Packet>(10);
                				clientRegister.get(packet.getPeerAddress()).put(packet.getPeerPort(),p);
                				resp = packet.toBuilder()
                		         		.setType(6)
                		                 .setPayload("Waiting for data".getBytes())
                		                 .create();
                				channel.send(resp.toBuffer(), router);
                			}
                	}else {
                		ArrayList<Packet> p = new ArrayList<Packet>(10);
                		clientRegister.put(packet.getPeerAddress(),new HashMap<Integer,ArrayList<Packet>>());
                		clientRegister.get(packet.getPeerAddress()).put(packet.getPeerPort(),p);
                		resp = packet.toBuilder()
        		         		.setType(6)
        		                 .setPayload("Waiting for data".getBytes())
        		                 .create();
                		channel.send(resp.toBuffer(), router);
                	}
                	break;
                case 3:
                	resp = handleRequest(packet,payload);
                	channel.send(resp.toBuffer(), router);
                	break;
                }
                
                	
                

            }
        }
        
    }
    
    private Packet handleRequest(Packet packet,String payload) {
    	//Is it GET OR POST
    	String request = payload.substring(0,payload.indexOf("http:")-1);
    	if(request.equals("POST")) {
    		boolean verbioseFound =false;
			 Scanner scanner = new Scanner(payload);
			 while (scanner.hasNextLine()) {
			   String line = scanner.nextLine();
			   if(line.equals("")) {
				   verbioseFound =true;
			   }
			  if(verbioseFound) {
				  bodyInfo+= line+"\n";
			  }
			 }
			 scanner.close();
    	}
    	return RequestFromClient(packet,payload,request);
    	
	}

	public Packet respondHandShake(Packet packet) throws IOException {
    	String message = "Hi : "+packet.getPeerAddress()+":"+packet.getPeerPort();
    	 Packet resp = packet.toBuilder()
         		.setType(1)
                 .setPayload(message.getBytes())
                 .create();
    	return resp;
    }
	
	public  Packet RequestFromClient(Packet packet, String info,String request) {
		String path;
		int indexOfEndOfURl=0;
		
		//GET what inside the '?' --> http://localhost?
			indexOfEndOfURl = info.indexOf("HTTP/1.0")-1;
		path = info.substring(info.indexOf("http:")+16,indexOfEndOfURl);
		
		if(path.length()>=2) {
			// FOR SECURITY
			if(path.charAt(1) == '.' && path.charAt(2) == '.') {
				System.out.println(" The Client tried to break the security");
				return fileNotReadable(packet);
			}else {
			return redirectInfo(packet,request,path);
			}
		}else {
			return redirectInfo(packet,request,path);
		}
	}
	
	public Packet redirectInfo(Packet packet, String request,String path) {
		Packet p =null;
		switch(request) {
		case "GET": 
					switch(path) {
					case "/"  :
					case "\\" : 
								p =getServerDefault(packet,path);
							  	break;
					default :
								p=getServerFile(packet,path);
								break;
					}
				break;
		
		case "POST":
					p=PostServer(packet,path);
					break;
		default : 
			break;
		}
		return p;
	}
	
	//IF PATH = '/'
	public  Packet getServerDefault(Packet packet, String path) {
		try { 
		File[] allfiles = new File(Paths.get(".").toAbsolutePath().normalize().toString()).listFiles();
		
		return responseWithContentTypeTEXT(packet,allfiles);
		
		}catch(Exception e) {
			return fileNotFound(packet);
		}
	}
	
	//IF PATH = '/SOMETHING'
	public synchronized Packet getServerFile(Packet packet, String path) {
		try{
				
				FileReader fileToread = new FileReader("src"+path);
				BufferedReader brfile = new BufferedReader(fileToread);
				String data="";
				String line;
				while ((line = brfile.readLine()) != null) {
            		data+= line+"\n";
				}
				fileToread.close();
				brfile.close();
				String resp ="HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+data.length()+"\r\n\r\n"+data;
				return RespToPacket(packet,resp);
		}catch(Exception e) {
			return fileNotFound(packet);
		}
	}
	
	private Packet RespToPacket(Packet packet, String resp) {
		double lgt = resp.length();
		double maxSize =1013;
		double count = Math.ceil(lgt /maxSize);
		Packet p =null;
		String message ="";
		for(int x =1;x<=count;x++) {
			if(x<count) {
			message = resp.substring((x-1)*1013,x*1013);
			}else {
				message = resp.substring((x-1)*1013,resp.length());
			}
			 p = new Packet.Builder()
                    .setType(4)
                    .setSequenceNumber(x)
                    .setPortNumber(packet.getPeerPort())
                    .setPeerAddress(packet.getPeerAddress())
                    .setPayload(message.getBytes())
                    .create();
			
			 ArrayList<Packet> listOfPacket = this.clientRegister.get(packet.getPeerAddress()).get(packet.getPeerPort());
			 Map<Integer,ArrayList<Packet>> map = this.clientRegister.get(packet.getPeerAddress());
			 listOfPacket.add(p);
			 map.put(packet.getPeerPort(),listOfPacket);
			 this.clientRegister.put(packet.getPeerAddress(),map);
		}
		return this.clientRegister.get(packet.getPeerAddress()).get(packet.getPeerPort()).get(0);
		
	}

	//Post 
	public  Packet PostServer(Packet packet, String path) {
		try(PrintWriter writer = new PrintWriter(new FileOutputStream("src"+path, false))){
			writer.println(bodyInfo);
			writer.close();
			String format = "{\n\"data\": \"{"+ bodyInfo +"}\",\n}";
			String resp = "HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+format.length()+"\r\n\r\n"+format;
			return RespToPacket(packet,resp);
		}catch(Exception e) {
			return fileNotFound(packet);
		}
	}
		
	private  Packet responseWithContentTypeTEXT(Packet packet, File[] allfiles) {
		String data="";
		for(File file : allfiles) {
			if(file.isFile()) {
				data += file.getName() + "\n";
			}
		}
		String answer = "HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+data.length()+"\r\n\r\n"+data;
		return RespToPacket(packet,answer);
	}

	private  Packet fileNotFound(Packet packet) {
		String resp ="HTTP/1.0 404 Not Found";
		return RespToPacket(packet,resp);
	}
	
    private  Packet fileNotReadable(Packet packet) {
    	String resp ="HTTP/1.0 403 Forbidden";
		return RespToPacket(packet,resp);
	}

    public static void main(String[] args) throws IOException {
    	while(true) {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(asList("port", "p"), "Listening port")
                .withOptionalArg()
                .defaultsTo("8007");

        OptionSet opts = parser.parse(args);
        int port = Integer.parseInt((String) opts.valueOf("port"));
        UDPS server = new UDPS();
        server.listenAndServe(port);
        }
    }
}