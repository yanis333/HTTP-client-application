import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class PostRequest {
	
	private ArrayList<String> header;
	private String url;
	private boolean Notverbiose;
	private InetAddress ip;
	private String response;
	private String body;
	private String option;
	private String file;
	
	public PostRequest(String[] args) throws FileNotFoundException, IOException {
		header = new ArrayList<String>();
		url ="";
		response= "";
		ip= null;
		Notverbiose = true;
		option = null;
		body = "";
		file = "";
		
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
		
		boolean d =false;
		boolean f =false;
		
		for(int x=0;x<args.length;x++) {
			 
			 if(args[x].equals("-v")) {
				 Notverbiose =false; 
			 }
			 
			 if(args[x].equals("-h") && (x+1)<args.length) {
				 if(!header.contains(args[x+1]+"\r\n")) {
					 header.add(args[x+1]+"\r\n");
				 }
			 }
			 if(args[x].equals("-d") && (x+1)<args.length) {
				 d=true;
				 body+=args[x+1];
			 }
			 if(args[x].equals("-f") && (x+1)<args.length) {
				 f=true;
				 try(BufferedReader br = new BufferedReader(new FileReader(args[x+1]))) {
					    
					    String line = br.readLine();
					    while (line != null) {
					        file+=line;
					        line = br.readLine();
					    }
					    br.close();
					}catch(Exception e) {
						System.out.println(e);
					}
			 }
			 
		 }
		if(f == true && d == true) {
			System.out.println("You can only have either -d or -f not both");
			System.out.println("The program will terminate");
			System.exit(0);
		}else if(f == true && d!=true){
			body =file;
		}
	}

	public void sendRequest() throws Exception, MalformedURLException  {
		ip = InetAddress.getByName(new URL(url)
                .getHost()); 
		 Socket socket = new Socket(ip,80);
		 
		 
		 InputStream in = socket.getInputStream();
		 OutputStream out = socket.getOutputStream();
		 
		 String headers ="";
		 for (String el : header) { 	
			 headers+=el;
	      }
		 
		 String request = "POST "+url+" HTTP/1.0\r\n"+headers+"Content-Length: "+body.length()+"\r\n\r\n"+body+"";
		 
		 out.write(request.getBytes());
		 out.flush();
		 
		 StringBuilder response = new StringBuilder();
		 
		 int data = in.read();
		 
		 while(data != -1) {
			 response.append((char) data);
			 data = in.read();
		 }
		 this.response = response.toString();
		 printResponse();
		 
		 in.close();
		 out.close();
		 socket.close();
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
