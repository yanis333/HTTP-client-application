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


	 public static void main(String[] args) throws Exception{
		 
		 getRequest("http://httpbin.org/json");
		 
		 //postRequest("http://httpbin.org/response-headers","");
		 
	    }
	 
	 public static void getRequest(String url) throws Exception {
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
		 System.out.println(response);
		 
		 in.close();
		 out.close();
		 socket.close();
	 }

	 public static void postRequest(String url,String body) throws Exception {
		 InetAddress ip = InetAddress.getByName(new URL(url)
                 .getHost()); 
		 Socket socket = new Socket(ip,80);
		 
		 
		 InputStream in = socket.getInputStream();
		 OutputStream out = socket.getOutputStream();
		 
		 String request = "POST "+url+" HTTP/1.0\r\n"
		 		+ "Content-Type:application/x-www-form-urlencoded\r\n"
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
		 System.out.println(response);
		 
		 in.close();
		 out.close();
		 socket.close();
	 }
}
