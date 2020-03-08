import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;

public class ServerThread extends Thread{

	private InputStreamReader isr;
	private BufferedReader br;
	private PrintWriter pw;
	private String bodyInfo = "";
	private Socket socket;
	
	public ServerThread(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
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
						  bodyInfo += String.valueOf((char)data);
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
		System.out.println("Client Disconnected");
		}catch(Exception e) {
			
		}
	}

	public  void RequestFromClient(String info) {
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
	
	public void redirectInfo(String request,String path) {

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
					PostServer(path);
					break;
		default : 
			break;
		}
	}
	
	//IF PATH = '/'
	public  void getServerDefault(String path) {
		try { 
		File[] allfiles = new File(Paths.get(".").toAbsolutePath().normalize().toString()).listFiles();
		
		responseWithContentTypeTEXT(allfiles);
		
		}catch(Exception e) {
			fileNotFound();
		}
	}
	
	//IF PATH = '/SOMETHING'
	public synchronized void getServerFile(String path) {
		try{
				FileReader fileToread = new FileReader("..\\src\\"+path);
				BufferedReader brfile = new BufferedReader(fileToread);
				String data="";
				String line;
				while ((line = brfile.readLine()) != null) {
            		data+= line+"\n";
				}
				fileToread.close();
				brfile.close();
            pw.write("HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+data.length()+"\r\n\r\n"+data);
            pw.close();
		}catch(Exception e) {
			fileNotFound();
		}
	}
	
	//Post 
	public  void PostServer(String path) {
		try(PrintWriter writer = new PrintWriter(new FileOutputStream("..\\src\\"+path, false))){
			writer.println(bodyInfo);
			writer.close();
			String format = "{\n\"data\": \"{"+ bodyInfo +"}\",\n}";
			pw.write("HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+format.length()+"\r\n\r\n"+format);
	        pw.close();
		}catch(Exception e) {
			fileNotFound();
		}
	}
		
	private  void responseWithContentTypeTEXT(File[] allfiles) {
		String data="";
		for(File file : allfiles) {
			if(file.isFile()) {
				data += file.getName() + "\n";
			}
		}
		pw.write("HTTP/1.1 200 OK\r\nContent-Type:text/html\r\nContent-Length:"+data.length()+"\r\n\r\n"+data);
		pw.close();
		
	}

	private  void fileNotFound() {
		pw.write("HTTP/1.0 404 Not Found");
		pw.close();
	}
	
    private  void fileNotReadable() {
		pw.write("HTTP/1.0 403 Forbidden");
		pw.close();
	}

}
