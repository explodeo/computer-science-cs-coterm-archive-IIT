package MorcomChristopherFinal;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

import package

public class TESTINGPHASE {
			//NAME MUST BE CHANGED OR -5 POINTS
	public static void main(String [] args) throws IOException{
		int mainmcount = 0;
		int mainlcount = 0; 	//	these are counting variables
		int mainccount = 0; 	//	they keep track of how many
		int mainvcount = 0; 	//	times a user enters a cmd.
		int mainscount = 0; 
		int maindcount = 0;
		int mainpcount = 0; 
		int mainqcount = 0;
		int mainocount = 0; 
		int mainfailcount = 0;
		int mainhelpcount = 0;	//used for finalstats() method.

		final String ENDFILE ="END_OF_FILE"; //if the last line in the file is changed, then you only have to edit the sbove statement
		
		MorcomChristopherCandidate c = new MorcomChristopherCandidate("n","n",'n',1,3.14159,"zljjp");

		//ArrayList<Candidate> candidateinfo = new ArrayList<Candidate>(1);
		/* 
			how to modify an arraylist
			ArrayList<Matrices> list = new ArrayList<Matrices>(1<<no. placeholders in arraylist);
			list.add( new candidate(//specify object here) ); //creates a new object and adds it to the list.
			ArrayList<Matrices>(//object location.//use a method here);
			list.
		*/
		File inputfile = new File("cipcs115.txt");
		FileOutputStream outfile = new FileOutputStream("cipcs115_finalstats.txt");
		PrintWriter pw = new PrintWriter(outfile);

		Scanner filescanner = new Scanner(inputfile);	//these are scanners for the user and to read the file
		String temp;									//this is used to store each individual line as a temp so it can be modified
		int candidatesleft = 0;							//this is used as a checker in the arraylist
		Scanner userscanner = new Scanner(System.in);
		String cmd; //this is where the user's commands are stored

		boolean done = false; //this is a bool value which breaks loops throughout the program
		/*		
		while (done = false) {
				//this is where the program scans the file into the arraylist
			if (filescanner.nextLine().equals(ENDFILE))

		}
		*/
		for(;;){ // EVERYTHING HAPPENS INSIDE THIS 'FOR' LOOP
			System.out.print("What would you like to do? (type 'help' or '?' for a list of commands.)");
			cmd = userscanner.next();
			if (cmd.equalsIgnoreCase("M")){
				//mcount++;
				System.out.print("What would you like to do? (type 'help' or '?' for a list of commands.)");
				cmd = userscanner.next();
				menu(cmd);
			} 
			if (cmd.equalsIgnoreCase("Q")){
				//finalstats();
				pw.close();
				System.exit(0);
			}
			if (cmd.equalsIgnoreCase("help")||cmd.equals("?")){
				System.out.println("Press 'M' to go into the menu. Press 'Q' to quit program.");
			} else {
			System.out.println("Command not found.");
			mainfailcount++;
			}
		}	
	}// main method is supposed to close here

	public static void menu(String menuitem) {
		//int mcount = 0;
		int lcount = 0; 	//	these are counting variables
		int ccount = 0; 	//	they keep track of how many
		int vcount = 0; 	//	times a user enters a cmd.
		int scount = 0; 
		int dcount = 0;
		int pcount = 0; 
		int qcount = 0;
		int ocount = 0; 
		int failcount = 0;
		int helpcount = 0;	//used for finalstats() method.

		if (menuitem.equalsIgnoreCase("L")) {
			lcount++;
			listall();
		} else if (menuitem.equalsIgnoreCase("C")) {
			ccount++;
			candidateinfo();
		} else if (menuitem.equalsIgnoreCase("V")) {
			vcount++;
			voteinfo();
		} else if (menuitem.equalsIgnoreCase("S")) {
			scount++;
			stateinfo();
		} else if (menuitem.equalsIgnoreCase("D")) {
			dcount++;
			dollarsSpentInfo();
		} else if (menuitem.equalsIgnoreCase("P")) {
			pcount++;
			partyInfo();
		} else if (menuitem.equalsIgnoreCase("Q")) {
			qcount++;
			return;
		} else if (menuitem.equalsIgnoreCase("O")) {
			ocount++;
			sortList();
		} else if (menuitem.equalsIgnoreCase("Help")|| menuitem.equals("?")) {
			//put a series of println's for each menu option. 
		} else {
			System.out.println("Invalid command. Type 'help' or '?' for a list of commands");
			failcount++;
		}
	}

	public static void listall(){
		return;
	}
	public static boolean candidateinfo(){
		return true;	//	t/f
	}
	public static boolean voteinfo() {
		return true;	//	t/f
	}
	public static boolean stateinfo(){
		return true;	//	t/f
	}
	public static boolean dollarsSpentInfo(){
		return true;	//	t/f
	}
	public static boolean partyInfo(){
		return true;	//	t/f
	}
	public static void sortList(){
		return;
	}
/*	public static void finalstats() {
		System.out.println(lcount);
		System.out.println(ccount);
		System.out.println(scount);
		System.out.println(dcount);
		System.out.println(pcount);
		System.out.println(ocount);
		System.out.println(failcount);
		System.out.println(helpcount);
		pw.out.println();		//do the same thing above so it prints to file
		pw.out.println();
		pw.out.println();
		pw.out.println();
		pw.out.println();
		pw.out.println();
		pw.out.println();
		pw.out.println();
		System.out.println(" ");	//write finalstats to display & file and show filename in display
		System.out.println("This data has been stored in: " + outfile);
	}
*/
}
