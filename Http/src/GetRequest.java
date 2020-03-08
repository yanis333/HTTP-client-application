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

public class GetRequest {

	private ArrayList<String> header;
	private String url ;
	private boolean Notverbiose;
	private String response;
	private InetAddress ip;
	private String option;
	private String status;
	private int counter;
	private String specialPath;
	
	public GetRequest(String[] args) {
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
		 Socket socket = new Socket(ip,8080);
		 
		 InputStream in = socket.getInputStream();
		 OutputStream out = socket.getOutputStream();
		 
		 String headers ="";
		 for (String el : header) { 	
			 headers+=el;
	      }
		 
		 String request = "GET "+url+" HTTP/1.0\r\n"
		 		+ headers.toString() +"\r\n";
		 
		 out.write(request.getBytes());
		 out.flush();
		 
		 StringBuilder response = new StringBuilder();
		 int data = in.read();
		 while(data != -1) {
			 response.append((char) data);
			 data = in.read();
		 }
		 
		 this.response = response.toString();
		 
		 status = response.substring(9,10);
		 
		 
		 
		 out.close();
		 in.close();
		 socket.close();
		 
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
