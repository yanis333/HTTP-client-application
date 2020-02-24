import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Https {
	
	static ServerSocket socketServer;
	static Socket socket;
	static InputStreamReader isr;
	static BufferedReader br;
	static PrintWriter pw;
	
	
	public static void main(String[] args) throws Exception {
		 socketServer = new ServerSocket(80);
		  socket = socketServer.accept();
		
		System.out.println("Client Connected!");
		
		 isr = new InputStreamReader(socket.getInputStream());
		 br = new BufferedReader(isr);
		 pw = new PrintWriter(socket.getOutputStream(),true);
		
		String info ="";
		String in ="";
		
		while(br.ready() && (in = br.readLine()) != null) {
			info += in.toString() +"\n";
		}
		
		RequestFromClient(info);
		

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
		for(int x=info.indexOf("http:");x<info.length();x++) {
			if(info.charAt(x) == ' ') {
				indexOfEndOfURl =x;
				break;
			}
		}
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
