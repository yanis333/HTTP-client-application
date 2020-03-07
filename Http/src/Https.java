import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class Https {
	
	static ServerSocket socketServer;
	static Socket socket;
	static InputStreamReader isr;
	static BufferedReader br;
	static PrintWriter pw;
	
	
	public static void main(String[] args) throws Exception {
		while(true) {
		 socketServer = new ServerSocket(80);
		  socket = socketServer.accept();
		
		System.out.println("Client Connected!");
		
		InputStream inp = socket.getInputStream();
		 pw = new PrintWriter(socket.getOutputStream(),true);
		
		String info ="";
		String in ="";
		StringBuilder response = new StringBuilder();
		 int data = inp.read();
		 int count=0;
		 int countLineFeed =0;
		 int countBody= 0;
		 int trackbody =0;
		 boolean check2 =true;
		 boolean leaveWhileLoop =false;
		 String request ="";
		 
		 while(data != -1) {
			 if(count == 10) {
				 request = response.toString().substring(0,response.toString().indexOf("http:")-1);
			 }
			 if(count >10) {
				 if(request.equals("GET")) {
					 if(countLineFeed == 2)
						 break;
				 }
				 else if(request.equals("POST")) {
					 if(countLineFeed ==2 && check2) {
						 countBody = Integer.parseInt(response.toString().substring(response.toString().indexOf("Content-Length")+16,response.toString().length()-1));					 
						 check2 =false;
					 }
					 if(countLineFeed ==3) {
						 if(trackbody == countBody) {
							 leaveWhileLoop =true;
						 }
						 trackbody++;
					 }
					 
				 }
			 }
			 response.append((char) data);
			 if(leaveWhileLoop) {
				 break;
			 }
			 data = inp.read();
			 if(data == 10 ) {
				 countLineFeed++;
				 }
			 count++;
		 }
		info = response.toString();
		RequestFromClient(info);
		socketServer.close();
		socket.close();
		System.out.println("Client Disconnected");
		}
	}

	public static void RequestFromClient(String info) {
		String request = "";
		String path;
		int indexOfEndOfURl=0;
		
		// IS IT GET OR POST
		for(int x=0;x<info.length();x++) {
			if(info.charAt(x) == ' ') {
				request	= info.substring(0,x);
				break;
			}
		}
		//GET what inside the '?' --> http://localhost?
			indexOfEndOfURl = info.indexOf("HTTP/1.0")-1;
		path = info.substring(info.indexOf("http:")+16,indexOfEndOfURl);
		
		if(path.length()>=2) {
			// FOR SECURITY
			if(path.charAt(1) == '.' && path.charAt(2) == '.') {
				System.out.println(" The Client tried to break the security");
				fileNotReadable();
			}else {
			redirectInfo(request,path);
			}
		}else {
			redirectInfo(request,path);
		}
	}
	
	public static void redirectInfo(String request,String path) {

		switch(request) {
		case "GET": 
					switch(path) {
					case "/"  :
					case "\\" : getServerDefault(path);
							  	break;
					default :
								getServerFile(path);
								break;
					}
				break;
		
		case "POST": 
					break;
		default : 
			break;
		}
	}
	
	//IF PATH = '/'
	public static void getServerDefault(String path) {
		try {
		File[] allfiles = new File(Paths.get(".").toAbsolutePath().normalize().toString()).listFiles();
		
		responseWithContentTypeTEXT(allfiles);
		
		}catch(Exception e) {
			fileNotFound();
		}
	}
	
	//IF PATH = '/SOMETHING'
	public static void getServerFile(String path) {
		try (FileReader fileToread = new FileReader("..\\src\\"+path)){
			 BufferedReader brfile = new BufferedReader(fileToread);
			String data="";
            String line;
            while ((line = brfile.readLine()) != null) {
            	data+= line+"\n";
            }
            pw.write("HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+data.length()+"\r\n\r\n"+data);
            pw.close();
            fileToread.close();
            brfile.close();
		}catch(Exception e) {
			fileNotFound();
		}
	}
	
	//HAITAM
	public void PostServer(String path) {
			
	}
		
	private static void responseWithContentTypeTEXT(File[] allfiles) {
		String data="";
		for(File file : allfiles) {
			if(file.isFile()) {
				data += file.getName() + "\n";
			}
		}
		pw.write("HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+data.length()+"\r\n\r\n"+data);
		pw.close();
		
	}

	private static void fileNotFound() {
		pw.write("HTTP/1.0 404 Not Found");
		pw.close();
	}
	
    private static void fileNotReadable() {
		pw.write("HTTP/1.0 403 Forbidden");
		pw.close();
	}

	

}
