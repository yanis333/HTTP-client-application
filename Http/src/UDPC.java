

public class UDPC {


	 public static void main(String[] args,int number) {
		//FileRequest(Notverbiose,"http://httpbin.org/anything",header,body);
		// postRequest(Notverbiose,"http://postman-echo.com/post",header,body);
		//getRequest(Notverbiose,"http://httpbin.org/absolute-redirect/1",header);
		/* args = new String[9];
		 args[0]="post";
		 args[1]="-v";
		 args[2]="-h";
		 args[3]="Content-Type:application/json";
		 args[4]="-h";
		 args[5]="Content-Type:application/json";
		 args[6]="-d";
		 args[7]="{Application:1}";*/
		 /*args[6]="-f";
		 args[7]="yanis.txt";*/
		 //args[8] = "http://localhost/post.txt";
		 /*args = new String[4];
		 args[0]="get";
		 args[1] = "-d";
		 args[2] = "{YANIS:TEXT}";
		 args[3]="http://localhost/yanis.txt";
		/*args = new String[2];
		 args[0]="get";
		 args[1] = "http://httpbin.org/redirect/2";*/
		 try {
				if(args.length==1 && args[0].toUpperCase().equals("HELP")){
					 Help.generalHelp();
				 }else if(args[0].toUpperCase().equals("GET")){
					 if(args.length == 2 && args[1].toUpperCase().equals("HELP")) {
						 Help.getHelp();
					 }else {
						 UDPC_Get get = new UDPC_Get(args,number);
						 if(get != null) {
							 get.sendRequest();}
						 else {
							 printError();
						 }
					 }
					 
				 }else if(args[0].toUpperCase().equals("POST")){
					 if(args.length == 2 && args[1].toUpperCase().equals("HELP")) {
						 Help.postHelp();
					 }else {
						 UDPC_Post post = new UDPC_Post(args,number);
						 if(post !=null) {
							 post.sendRequest();
						 }else {
							 printError();
						 }
						 
						 
					 }
				 }else {
					 printError();
				 }
				
			 
			 
		 }catch(Exception e) {
			 System.out.println(e);
		 }
		 
	 }
	 
	 public static void printError() {
		 System.out.println("Enter a correct cURL command to write : java Httpc help");
	 }

}
