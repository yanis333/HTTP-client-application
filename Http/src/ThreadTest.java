public class ThreadTest {
	
	
	public static void main(String[] args) throws Exception {
		String[] args1 = new String[5];
		args1[0] = "post";
		args1[1] = "-v";
		args1[2] = "-d";
		args1[3] = "{"
				+"FIRST PART"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,,\n\n"
				+ "Second Part"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,,\n\n"
				+"Third PART"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,\n\n"
				+"Fourth PART"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,"
				+ "Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,Applicaiton_Thread_POST2,}";
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
		
		Thread one = new Thread(() -> UDPC.main(args1,1));
        Thread two = new Thread(() -> UDPC.main(args2,2));
       Thread three = new Thread(() -> UDPC.main(args3,3));
        Thread four = new Thread(() -> UDPC.main(args4,4));
        Thread five = new Thread(() -> UDPC.main(args5,5));
        Thread six = new Thread(() -> UDPC.main(args6,6));

        one.start();
        two.start();
        three.start();
        four.start();
        five.start();
        six.start();
        
	}


}
