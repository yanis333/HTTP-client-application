import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
	
	
	public static void main(String[] args) throws Exception {
		
		 socketServer = new ServerSocket(80);
		 while(true) {
		 Socket socket = socketServer.accept();
		 System.out.println("Client Connected");
		 new ServerThread(socket).start();
		 }
		
	}


}
