/* Christopher Morcom
 * A20385764
 * CS115-02 */

/*	This program reads a file with information pertaining to candidates running for election offices in each (listed) state.
	It comtains a variety of methods which display information about a state or candidate(s), or candidates in a state/election race.
	This program also counts user inputs (i.e. the amount of times the user calls a method in the project).
*/
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;

public class MorcomChristopher {
	public static void main(String [] args) throws IOException{
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
		final int MAXNUMOFCANDIDATES = 50;		//self-explanatory
		final int MAXNUMOFSTATES = 20;			//this can be assumed by the project specifications
		final int MAXRUNCOUNT = 100;			//max no. runs the program will take.
		final int NUMOFCOUNTERS = 9;	//9 is due to the no. counting vars
		ArrayList<Integer> menucounter = new ArrayList<Integer>(NUMOFCOUNTERS);
		
		File inputfile = new File("cipcs115.txt");
		Scanner filescanner = new Scanner(inputfile);	//these are scanners for the user and to read the file
		
		String temp;									//this is used to store each individual line as a temp so it can be modified
		String ignoredline;								//this is used to ignore comment lines from the file
		int candidatesleft = 0;							//this is used as a checker in the arraylist
		Scanner userscanner = new Scanner(System.in);
		String cmd; //this is where the user's commands are stored

		int loops;
		String state = "";

		ArrayList<MorcomChristopherCandidate> candidatelist = new ArrayList<MorcomChristopherCandidate>();

		String newlineCharStorage; // when using Scanner.nextInt() method, the newline character is not consumed.
		
		String filename = "cipcs115_NoCommentedLines.txt";
		FileOutputStream tempcandfile = new FileOutputStream(filename, false);
		PrintWriter fileprinter = new PrintWriter(tempcandfile);

		boolean done = false;
		while(!done){ 		//This loop here compiles the file into a list that I know can be read properly by the next loop
			temp = filescanner.nextLine();
			if (temp.equals(ENDFILELINE)) {	//if .txt file ends
				done = true;
			} 
			if (temp.charAt(0) == '#') {	//gets rid of comment lines
				//do nothing
			} else {
				fileprinter.println(temp);
			}
		}
		fileprinter.close();	// closes printwriter so no issues occur

		File cipcs115file = new File(filename);
		Scanner tempfilescanner = new Scanner(cipcs115file);
		String ignorethis;
		done = false;
		while (!done) {	//this is where the program scans the temp file into the arraylist
			state = tempfilescanner.nextLine();
			if (state.equalsIgnoreCase(ENDFILELINE)) {	//last file line is in the state field. 
				done = true;
				break;
			}
			loops = tempfilescanner.nextInt();
			int loopcount = 0;
			for (loopcount = 0; loopcount < loops; loopcount++) {
				candidatelist.add(new MorcomChristopherCandidate(tempfilescanner.next(), tempfilescanner.next(), tempfilescanner.next().toLowerCase().charAt(0), tempfilescanner.nextInt(), tempfilescanner.nextDouble(), tempfilescanner.nextLine(), state));
			}
		} //scan into arraylist completed
		int forloopcounter;	//this is used for counting inside the cascaded for loop below.
		for(;;){
			System.out.print("What would you like to do? (type 'help' or '?' for a list of commands.)");
			cmd = userscanner.next();
			ignorethis = userscanner.nextLine();	//this ensures any spaces in the command are not taken in by the next scan.next().
			if (cmd.equalsIgnoreCase("M")){ //calls menu()
				//mainmcount++; //var is no longer needed
				System.out.print("What would you like to do? (type 'help' or '?' for a list of commands.)");
				cmd = userscanner.next();
				ignorethis = userscanner.nextLine();	//this ensures any spaces in the command are not taken in by the next scan.next().
				menucounter = menu(cmd, candidatelist);
				mainlcount += menucounter.get(0); 
				mainccount += menucounter.get(1); 
				mainvcount += menucounter.get(2); 
				mainscount += menucounter.get(3); 
				maindcount += menucounter.get(4);
				mainpcount += menucounter.get(5); 
				mainqcount += menucounter.get(6);
				mainocount += menucounter.get(7); 
				mainfailcount += menucounter.get(8);
				mainhelpcount += menucounter.get(9);
			} 
			else if (cmd.equalsIgnoreCase("Q")){ //calls finalstats()
				finalstats(mainlcount, mainccount, mainvcount, mainscount, maindcount, mainpcount, mainqcount, mainocount, mainfailcount, mainhelpcount); // mainmcount removed
				System.exit(0);	//makes it so that the program 
			}
			else if (cmd.equalsIgnoreCase("help")||cmd.equals("?")){	// no method called
				System.out.println("Press 'M' to go into the menu. Press 'Q' to quit program.");
				mainhelpcount++;
			}
			else {
				System.out.println("Command not found."); 
				mainfailcount++;
			}
		}	//end user inputs
	}// main method closes here

	public static ArrayList<Integer> menu(String menuitem, ArrayList<MorcomChristopherCandidate> candylist) { //gotta pass the list to every method
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
		ArrayList<Integer> countervars = new ArrayList<Integer>(); // used to return the counting vars above back to main();
		for(int cvc = 0;cvc<=9;cvc++){ //cannot use the constant: NUMOFCOUNTERS (which equals 9) because it is not global.
			countervars.add(0);
		}
		Scanner menuscanuser = new Scanner(System.in);
		for(;;){ //allows for infinite 
			if (menuitem.equalsIgnoreCase("L")) {
				lcount++;
				listall(candylist);
			} else if (menuitem.equalsIgnoreCase("C")) {
				ccount++;
				candidateinfo(candylist);
			} else if (menuitem.equalsIgnoreCase("V")) {
				vcount++;
				voteinfo(candylist);
			} else if (menuitem.equalsIgnoreCase("S")) {
				scount++;
				stateinfo(candylist);
			} else if (menuitem.equalsIgnoreCase("D")) {
				dcount++;
				dollarsSpentInfo(candylist);
			} else if (menuitem.equalsIgnoreCase("P")) {
				pcount++;
				partyInfo(candylist);
			} else if (menuitem.equalsIgnoreCase("Q")) {
				qcount++;					
				countervars.set(0, lcount);
				countervars.set(1, ccount);
				countervars.set(2, vcount);
				countervars.set(3, scount);
				countervars.set(4, dcount);
				countervars.set(5, pcount);
				countervars.set(6, qcount);
				countervars.set(7, ocount);
				countervars.set(8, helpcount);
				countervars.set(9, failcount);
				System.out.println("(You are back in the main method.)");
				return countervars;	//this will return the array to main to be used in counting inputs
			} else if (menuitem.equalsIgnoreCase("O")) {
				ocount++;
				sortList(candylist);
			} else if (menuitem.equalsIgnoreCase("Help")|| menuitem.equals("?")) {
				help();	//put a series of println's for each menu option. 
				helpcount++;
			} else {
				System.out.println("Invalid command. Type 'help' or '?' for a list of commands");
				failcount++;
			}
			System.out.print("What would you like to do? (type 'help' or '?' for a list of commands.)");
			menuitem = menuscanuser.next();
		}
	}

	public static void listall(ArrayList<MorcomChristopherCandidate> candlist){
		Scanner listallscanner = new Scanner(System.in);
		String listallcmd; 
		boolean done = false;
		while (!done) {
			System.out.print("Enter 'r', 'd', 'i', 'o' to view all candidates from a certain party. Or enter 'all' to view all candidates: ");
			listallcmd = listallscanner.next();
			if (listallcmd.equalsIgnoreCase("r")){
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
					if (candlist.get(listcounter).getParty() == 'r') {
						System.out.println(candlist.get(listcounter).getCandidateName()+" "+candlist.get(listcounter).getParty()+" "+candlist.get(listcounter).getMotto());
					}
				}
				done = true;
			} else if (listallcmd.equalsIgnoreCase("d")) {
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
					if (candlist.get(listcounter).getParty() == 'd') {
						System.out.println(candlist.get(listcounter).getCandidateName()+" "+candlist.get(listcounter).getParty()+" "+candlist.get(listcounter).getMotto());
					}
				}
				done = true;
			} else if (listallcmd.equalsIgnoreCase("i")) {
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
					if (candlist.get(listcounter).getParty() == 'i') {
						System.out.println(candlist.get(listcounter).getCandidateName()+" "+candlist.get(listcounter).getParty()+" "+candlist.get(listcounter).getMotto());
					}
				}
				done = true;
			} else if (listallcmd.equalsIgnoreCase("o")) {
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
					if (candlist.get(listcounter).getParty() == 'o') {
						System.out.println(candlist.get(listcounter).getCandidateName()+" "+candlist.get(listcounter).getParty()+" "+candlist.get(listcounter).getMotto());
					}
				}
				done = true;
			} else if (listallcmd.equalsIgnoreCase("all")) {
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
					System.out.println(candlist.get(listcounter).getCandidateName()+" "+candlist.get(listcounter).getParty()+" "+candlist.get(listcounter).getMotto());
				}
				done = true;
			} else {
				System.out.println("Invalid entry.");
				done = false;	//line not needed. overloads the variable to make sure loop doesnt break.
			}
		}
		return;
	}
	public static boolean candidateinfo(ArrayList<MorcomChristopherCandidate> candlist){
		Scanner candinfoscanner = new Scanner(System.in);
		System.out.print("Please type a candidate last name: ");
		String candinfoname = candinfoscanner.next(); 
		boolean returnvalue = false;
		int test = 0;	//for some stupid reason, this method wont work if the boolean is mentioned inside the for loop
		for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
			if(candlist.get(listcounter).getCandidateName().equalsIgnoreCase(candinfoname)) {
				candlist.get(listcounter).displayCandidate();
				test = 1;;
			} 
		}
		if (test == 0) {
			System.out.println("No candidate found.");
			return returnvalue;
		} else {
			returnvalue = true;
		}
		return returnvalue;
	}
	public static boolean voteinfo(ArrayList<MorcomChristopherCandidate> candlist) {	// used the total no. votes here so I dont have to reprocess it in the method
		Scanner stateinfoscanner = new Scanner(System.in);
		boolean returnvalue = false;
		int maxVotes = 0;
		int test = 0;	//for some stupid reason, this method wont work if the boolean is mentioned inside the for loop
		int nocands;
		double percentVotes; //for some reason, if the no. votes for a candidate are too small, they will be seen as getting 0% of the total votes.

		System.out.print("Type 'all' to view all candidates by election race." + '\n' + "Or type an election race to search for (eg. Illinois President): ");
		String electionrace = stateinfoscanner.nextLine(); 
		if (!electionrace.equalsIgnoreCase("all")){
		}
		if (electionrace.equalsIgnoreCase("all")){
			for (int lstcounter = 0; lstcounter < candlist.size(); ) {
				if (!electionrace.equalsIgnoreCase(candlist.get(lstcounter).getState()+ " " + candlist.get(lstcounter).getElectionOffice())) {
					electionrace = candlist.get(lstcounter).getState()+ " " + candlist.get(lstcounter).getElectionOffice();
					System.out.println("--------------  "+electionrace+"  --------------");	//new elec.race
					nocands = 0;
					for (int b = 0; b < candlist.size(); b++) {
						if (electionrace.equalsIgnoreCase(candlist.get(b).getState() + " " + candlist.get(b).getElectionOffice())) { //gets loop count for each state and avg & total $spent
							maxVotes += candlist.get(b).getNumVotes();
							nocands++;
						}
					}
					System.out.println("Election Race Total Votes: "+maxVotes +" votes"); //prints out each elec. race's data
				}
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) { //prints out each candidate 
					if (electionrace.equalsIgnoreCase(candlist.get(listcounter).getState() + " " + candlist.get(listcounter).getElectionOffice())) {
						System.out.println("Name: " + candlist.get(listcounter).getCandidateName() + "\n\t" + "Amt. Votes: " + candlist.get(listcounter).getNumVotes() + " (" + ((100*(candlist.get(listcounter).getNumVotes()))/maxVotes) + "% of votes in " + candlist.get(listcounter).getState() + ")");
						lstcounter++;
					}
				}
			}
			test = 1; returnvalue = true;
		} else {
			nocands = 0; //no. cands.. used to calc avg voes
			for (int x = 0; x < candlist.size(); x++) {
				if (electionrace.equalsIgnoreCase(candlist.get(x).getState() + " " + candlist.get(x).getElectionOffice())) {
					maxVotes += candlist.get(x).getNumVotes();
					nocands++;
				}
			} //found total votes in state
			for (int bb = 0; bb < candlist.size(); bb++) {
				if (electionrace.equalsIgnoreCase(candlist.get(bb).getState() + " " + candlist.get(bb).getElectionOffice())) {
					System.out.println("Name: " + candlist.get(bb).getCandidateName() + "\n\t" + "Amt. Votes: " + candlist.get(bb).getNumVotes() + " (" + ((100*candlist.get(bb).getNumVotes())/maxVotes) + "% of votes in " + candlist.get(bb).getState() + ")");
					test = 1; returnvalue = true;
				}
			}
		}
		if (test == 0) {
			System.out.println("No election race found.");
			returnvalue = false;
		}
		return returnvalue;
	}
	public static boolean stateinfo(ArrayList<MorcomChristopherCandidate> candlist) {
		Scanner stateinfoscanner = new Scanner(System.in);
		System.out.print("Please type a state to search for: ");
		String state = stateinfoscanner.next(); 
		boolean returnvalue = false;
		double maxexpenses = 0;
		int test = 0;	//for some stupid reason, this method wont work if the boolean is mentioned inside the for loop
		int nocands;
		double avgexps;
		if (state.equalsIgnoreCase("all")){
			returnvalue = true;
			for (int lstcounter = 0; lstcounter < candlist.size();) {
				if (!state.equals(candlist.get(lstcounter).getState())) {
					state = candlist.get(lstcounter).getState();
					System.out.println("--------------  "+state+"  --------------");	//new state
					nocands = 0;
					for (int b = 0; b < candlist.size(); b++) {
						if (candlist.get(b).getState().equalsIgnoreCase(state)) { //gets loop count for each state and avg & total $spent
							maxexpenses += candlist.get(b).getDollarsSpent();
							nocands++;
						}
					}
					avgexps = maxexpenses/nocands;
					System.out.println("State Total Expenses: $"+maxexpenses + "\n" + "State Average Expenses: $"+avgexps); //prints out each state's data
				}
				for (int listcounter = 0; listcounter < candlist.size(); listcounter++) { //prints out each candidate 
					if (candlist.get(listcounter).getState().equalsIgnoreCase(state)) {
						System.out.println(candlist.get(listcounter).getCandidateName() + "\t" + candlist.get(listcounter).getElectionOffice() + "\t" + candlist.get(listcounter).getParty() + "\t$" +  candlist.get(listcounter).getDollarsSpent());
						lstcounter++;
					}
				}
			}
			test = 1; returnvalue = true;
		} else {
			nocands = 0; //no. cands.. used to calc avg $spent
			for (int x = 0; x < candlist.size(); x++) {
				if (candlist.get(x).getState().equalsIgnoreCase(state)) {
					maxexpenses += candlist.get(x).getDollarsSpent();
					nocands++;
				}
			}
			avgexps = maxexpenses/nocands;
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if(candlist.get(listcounter).getState().equalsIgnoreCase(state)) {
					System.out.println(candlist.get(listcounter).getCandidateName() + "\t" + candlist.get(listcounter).getElectionOffice() + "\t" + candlist.get(listcounter).getParty() + "\t$" +  candlist.get(listcounter).getDollarsSpent() + "\n\t" + 
									   "State Total Expenses: $"+maxexpenses + "\n\t" + "State Average Expenses: $"+avgexps); 

					test = 1; returnvalue = true;
				} 
			}
		}
		if (test == 0) {
			System.out.println("No state found.");
			returnvalue = false;
		}
		return returnvalue;
	}
	public static boolean dollarsSpentInfo(ArrayList<MorcomChristopherCandidate> candlist){
		Scanner scan = new Scanner(System.in);
		System.out.print("Please type a Candidate Last Name to search for or type 'all' to see all candidates' information: ");
		String lastname = scan.next(); 
		boolean returnvalue = false;
		int test = 0;
		int listbreak = candlist.size();
		if (lastname.equalsIgnoreCase("all")) {
			for (int listcount = 0; listcount < listbreak; listcount++) {
				System.out.println(candlist.get(listcount).getCandidateName() + "\n\t Office: " + candlist.get(listcount).getElectionOffice() + "\n\t Party: " + candlist.get(listcount).getParty() + "\n\t Dollars spent: $"+candlist.get(listcount).getDollarsSpent());
			}
			test =1; returnvalue = true;
		} else {
			for (int listcounter = 0; listcounter < listbreak; listcounter++) {
				if (lastname.equalsIgnoreCase(candlist.get(listcounter).getCandidateName())) {
					System.out.println("Name: " + candlist.get(listcounter).getCandidateName() + "\t Dollars spent: $"+candlist.get(listcounter).getDollarsSpent());
				test =1; returnvalue = true;
				} 
			}

			if (test == 0) {
				returnvalue = false;
				System.out.println("No candidate(s) found.");
			}
		}
		return returnvalue;
	}
	public static boolean partyInfo(ArrayList<MorcomChristopherCandidate> candlist){
		Scanner partyinfoscanner = new Scanner(System.in);
		String partyinfocmd;
		boolean returnvalue = false;
		System.out.print("Enter 'r', 'd', 'i', 'o' to view all candidates from a certain party or enter 'all' to view all candidates. ");
		partyinfocmd = partyinfoscanner.next();
		if (partyinfocmd.equalsIgnoreCase("r")){
			System.out.println("-------------  Republican Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'r') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			returnvalue = true;
		} else if (partyinfocmd.equalsIgnoreCase("d")) {
			System.out.println("-------------  Democrat Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'd') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			returnvalue = true;
		} else if (partyinfocmd.equalsIgnoreCase("i")) {
			System.out.println("-------------  Independent Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'i') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			returnvalue = true;
		} else if (partyinfocmd.equalsIgnoreCase("o")) {
			System.out.println("-------------  Other Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'o') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			returnvalue = true;
		} else if (partyinfocmd.equalsIgnoreCase("all")) {
			System.out.println("-------------  Republican Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'r') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			System.out.println("-------------  Democrat Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'd') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			System.out.println("-------------  Independent Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'i') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			System.out.println("-------------  Other Party Candidates  ------------- ");
			for (int listcounter = 0; listcounter < candlist.size(); listcounter++) {
				if (candlist.get(listcounter).getParty() == 'o') {
					System.out.println("Name: "+candlist.get(listcounter).getCandidateName()+"\t Election Race: "+candlist.get(listcounter).getState()+" "+candlist.get(listcounter).getElectionOffice());
				}
			}
			returnvalue = true;
		} else {
			System.out.println("No Party Found.");
			returnvalue = false;	//line not needed. overloads the variable to make sure loop doesnt break.
		}
		return returnvalue;	//	t/f
	}
	public static void sortList(ArrayList<MorcomChristopherCandidate> candlist){ //this method does not work, but this is what I could get done.
		ArrayList<MorcomChristopherCandidate> templist = new ArrayList<MorcomChristopherCandidate>(candlist.size());
		templist = candlist; //duplicates candidate list so that values can be removed
		ArrayList<MorcomChristopherCandidate> sortedlist = new ArrayList<MorcomChristopherCandidate>();
		int deletenumber = 0; //removes a sorted value from the templist
		int loopcount = 0;
		int maxloop = templist.size();
		MorcomChristopherCandidate temp = new MorcomChristopherCandidate(templist.get(0).getCandidateName(), templist.get(0).getElectionOffice(), templist.get(0).getParty(), templist.get(0).getNumVotes(), templist.get(0).getDollarsSpent(), templist.get(0).getMotto(), templist.get(0).getState());
		System.out.println(temp.candidateToString()); //test line
		for (int candnumber = 0; candnumber < candlist.size(); candnumber++) { //this is a simple sort
			deletenumber = 0; loopcount = 0; //not sure if necessary
			temp = templist.get(0);
			for (loopcount = 0; loopcount < maxloop; loopcount++)  { //gets next candidate to put at the top of the sorted list
				if (temp.getCandidateName().compareTo(templist.get(loopcount).getCandidateName()) > 0) {
					temp = templist.get(loopcount); //if more alphabetically correct, reassign temp 
					deletenumber = loopcount; //used to make templist shorter as each value is sorted
				} else if ((temp.getCandidateName().compareTo(templist.get(loopcount).getCandidateName()) == 0)) { //if both names are the same
					sortedlist.add(templist.get(loopcount)); //adds same name cand. to sorted list then removes them from temp list
					templist.remove(loopcount);

				} else {
					//do nothing
				}
				sortedlist.add(temp); //assign next name to the sorted list 
				templist.remove(deletenumber);
				maxloop = templist.size();
			}
		}
		String printline;
		for (int dispcand = 0; dispcand < candlist.size(); dispcand++) {
			printline = sortedlist.get(dispcand).candidateToString();
			System.out.println(printline);
			//System.out.println(sortedlist.get(dispcand).toString()); //THIS DOESN'T WORK.... WHY?????
		}
		return;
	}
	public static void finalstats(int num2, int num3, int num4, int num5, int num6, int num7, int num8, int num9, int num10, int num11) throws IOException {
		String outfilename = "cipcs115_finalstats.txt";
		FileOutputStream outfile = new FileOutputStream(outfilename, false);
		PrintWriter pw = new PrintWriter(outfile);
		System.out.println("There have been " + num2 + " calls to the listAll() method.");	//l
		System.out.println("There have been " + num3 + " calls to the candidateInfo() method.");	//c
		System.out.println("There have been " + num4 + " calls to the voteInfo() method.");	//v
		System.out.println("There have been " + num5 + " calls to the stateInfo() method.");	//s 				**THESE ARE THE COUNTERS THAT WILL BE PRINTED OUT TO CONSOLE**
		System.out.println("There have been " + num6 + " calls to the dollarsSpentInfo() method.");	//d
		System.out.println("There have been " + num7 + " calls to the partyInfo() method.");	//p
		System.out.println("There have been " + num8 + " calls to the finalstats().");	//q
		System.out.println("There have been " + num9 + " calls to the sortList() method.");	//o
		System.out.println("There have been " + num10 + " invalid inputs.");	//fails
		System.out.println("There have been " + num11 + " calls to the help() method.");	//helps
		pw.println("There have been " + num2 + " calls to the listAll() method.");	//l
		pw.println("There have been " + num3 + " calls to the candidateInfo() method.");	//c
		pw.println("There have been " + num4 + " calls to the voteInfo() method.");	//v
		pw.println("There have been " + num5 + " calls to the stateInfo() method.");	//s 				**THESE ARE THE COUNTERS THAT WILL BE PRINTED OUT TO FINALSTATS.txt**
		pw.println("There have been " + num6 + " calls to the dollarsSpentInfo() method.");	//d
		pw.println("There have been " + num7 + " calls to the partyInfo() method.");	//p
		pw.println("There have been " + num8 + " calls to the finalstats() method.");	//q
		pw.println("There have been " + num9 + " calls to the sortList() method.");	//o
		pw.println("There have been " + num10 + " invalid inputs.");	//fails
		pw.println("There have been " + num11 + " calls to the help() method.");	//helps
		
		System.out.println(" ");	//write finalstats to display & file and show filename in display
		System.out.println("This data has been stored in: " + outfilename);
		pw.close();
		return;
	}
	public static void help() {
		System.out.println("l 	--Lists all candidate data available by party or all");	//this is the help method for use in menu(); to show the valid user inputs
		System.out.println("c 	--Displays information for a particular candidate");
		System.out.println("v 	--Displays voting information for a particular candidate");
		System.out.println("s 	--Displays state information");						
		System.out.println("d 	--Displays dollars spent information");				
		System.out.println("p 	--Displays party information");						
		System.out.println("o 	--Sorts candidates by last name");
		System.out.println("q 	--Quits and returns to main()");
	}
}