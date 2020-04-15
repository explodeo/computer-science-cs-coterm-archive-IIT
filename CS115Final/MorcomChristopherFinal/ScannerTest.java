/* Christopher Morcom
 * A20385764
 * CS115-02 */

//This program does......

import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class ScannerTest {
			//NAME MUST BE CHANGED OR -5 POINTS
	public static void main(String [] args) throws IOException{
		//int mainmcount = 0;	//	calling menu() is not counted
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

		final String ENDFILELINE ="END_OF_FILE"; //if the last line in the file is changed, then you only have to edit the sbove statement
		final int MAXNUMOFCANDIDATES = 50;
		final int MAXNUMOFSTATES = 20;			//this can be assumed by the project specifications
		final int MAXRUNCOUNT = 100;
		//>>>MorcomChristopherCandidate c = new MorcomChristopherCandidate("n","n",'n',1,3.14159,"zljjp");		THIS IS A TEST LINE

		final int numofcounters = 9;	//9 is due to the no. counting vars
		ArrayList<Integer> menucounter = new ArrayList<Integer>(numofcounters);
		/* 
			how to modify an arraylist
			ArrayList<objecttype> arraylistname = new ArrayList<objecttype>();
			list.add( new candidate(//specify object here) ); //creates a new object and adds it to the list.
		*/
		File inputfile = new File("cipcs115.txt");
		Scanner filescanner = new Scanner(inputfile);	//these are scanners for the user and to read the file
		String temp;									//this is used to store each individual line as a temp so it can be modified
		String tempint = "0";
		String ignoredline;								//this is used to ignore comment lines from the file
		int candidatesleft = 0;							//this is used as a checker in the arraylist
		Scanner userscanner = new Scanner(System.in);
		String cmd; //this is where the user's commands are stored

		boolean done = false;
		int loops = 0;
		String state = "";

		ArrayList<MorcomChristopherCandidate> candidatelist = new ArrayList<MorcomChristopherCandidate>();
		
		do {	//this is where the program scans the file into the arraylist
			if  (filescanner.nextLine().equals(ENDFILELINE) || filescanner.nextLine().equals(ENDFILELINE+"\n")) { //original cipcs115.txt has a newlin char after the "END_OF_FILE" line.
				done = true;
				System.out.println("222222222"); //test line
			}
			if (filescanner.nextLine().charAt(0) =='#') {	//if line starts with '#' then it is ignored
				ignoredline = filescanner.nextLine();
			} 
			//if line is a state, number, or candidate, do this:
			

		} while (!done);
	}// main method is supposed to close here
}