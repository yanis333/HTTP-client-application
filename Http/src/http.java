import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class http {


	 public static void main(String[] args) {
		 try {
			 if(args.length>=1) {
				 
				 // HANDLE VERBIOSE
				 boolean Notverbiose =true;
				 if(args.length>=2 && args[1].equals("-v")) {
					 Notverbiose =false;
				 }
				 
				 //HANDLE REQUEST
				 if(args[0].toUpperCase().equals("GET")) {
					 getRequest(Notverbiose,"http://httpbin.org/get?course=networking&assignment=1");
					 
				 }else if(args[0].toUpperCase().equals("POST")) {
					postRequest(Notverbiose,"http://httpbin.org/response-headers","");
					 
				 }else {
					 System.out.println("You entered a wrong cURL Command");
				 }
				 
			 }else {
				 System.out.println("Please enter some parameters");
			 }
			 }catch(Exception e){
					  System.out.println(e);
				  }
		 
		 
	    }
	 
	 public static void getRequest(boolean Notverbiose,String url) throws Exception {
		 InetAddress ip = InetAddress.getByName(new URL(url)
                 .getHost()); 
		 Socket socket = new Socket(ip,80);
		 
		 InputStream in = socket.getInputStream();
		 OutputStream out = socket.getOutputStream();
		 
		 String request = "GET "+url+" HTTP/1.0\r\n\r\n";
		 
		 out.write(request.getBytes());
		 out.flush();
		 
		 StringBuilder response = new StringBuilder();
		 
		 int data = in.read();
		 
		 while(data != -1) {
			 response.append((char) data);
			 data = in.read();
		 }
		 
		 String responseToString = response.toString();
		 if(Notverbiose) {
			 responseToString = responseToString.substring(responseToString.indexOf('{'),responseToString.length());
		 }
		 
		 System.out.println(responseToString);
		 
		 in.close();
		 out.close();
		 socket.close();
	 }

	 public static void postRequest(boolean Notverbiose,String url,String body) throws Exception {
		 InetAddress ip = InetAddress.getByName(new URL(url)
                 .getHost()); 
		 Socket socket = new Socket(ip,80);
		 
		 
		 InputStream in = socket.getInputStream();
		 OutputStream out = socket.getOutputStream();
		 
		 String request = "POST "+url+" HTTP/1.0\r\n"
		 		+ "Content-Type:application/json\r\n"
		 		+ "Content-Length: "+body.length()+" \r\n\r\n"
		 				+ body;
		 
		 out.write(request.getBytes());
		 out.flush();
		 
		 StringBuilder response = new StringBuilder();
		 
		 int data = in.read();
		 
		 while(data != -1) {
			 response.append((char) data);
			 data = in.read();
		 }
		 
		 String responseToString = response.toString();
		 if(Notverbiose) {
			 responseToString = responseToString.substring(responseToString.indexOf('{'),responseToString.length());
		 }
		 
		 System.out.println(responseToString);
		 
		 in.close();
		 out.close();
		 socket.close();
	 }
}
