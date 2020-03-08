public class ThreadTest {
	
	
	public static void main(String[] args) throws Exception {
		String[] args1 = new String[5];
		args1[0] = "post";
		args1[1] = "-v";
		args1[2] = "-d";
		args1[3] = "{Applicaiton_Thread_POST2}";
		args1[4] = "http://localhost/yanis.txt";
		
		
		String[] args2 = new String[5];
		args2[0] = "post";
		args2[1] = "-v";
		args2[2] = "-d";
		args2[3] = "{Applicaiton_Thread_POST1}";
		args2[4] = "http://localhost/yanis.txt";
		
		
		String[] args3 = new String[3];
		args3[0] = "get";
		args3[1] = "-v";
		args3[2] = "http://localhost/";
		
		String[] args4 = new String[3];
		args4[0] = "get";
		args4[1] = "-v";
		args4[2] = "http://localhost/yanis.txt";
		
		String[] args5 = new String[3];
		args5[0] = "get";
		args5[1] = "-v";
		args5[2] = "http://localhost/doesNotExist.txt";
		
		String[] args6 = new String[3];
		args6[0] = "get";
		args6[1] = "-v";
		args6[2] = "http://localhost/../doesNotExist.txt";
		
		Thread one = new Thread(() -> httpc.main(args1));
        Thread two = new Thread(() -> httpc.main(args2));
        Thread three = new Thread(() -> httpc.main(args3));
        Thread four = new Thread(() -> httpc.main(args4));
        Thread five = new Thread(() -> httpc.main(args5));
        Thread six = new Thread(() -> httpc.main(args6));

        one.start();
        two.start();
        three.start();
        four.start();
        five.start();
        six.start();
        
	}


}
