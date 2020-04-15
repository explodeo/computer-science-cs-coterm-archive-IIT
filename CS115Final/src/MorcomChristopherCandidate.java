package MorcomChristopherFinal2;

public class MorcomChristopherCandidate {

	public String name;
	private String office;
	private char party;
	private int numvotes;
	private double expenses;
	private String motto;
	
	public MorcomChristopherCandidate(String n, String ofce, char p, int nv, double e, String m) {
		name = n;
		office = ofce;
		party = p;
		numvotes =nv;
		expenses = e;
		motto = m;
	}
	public String getCandidateName(){
		return name;
	}
	public String getElectionOffice(){
		return office;
	}
	public char getParty(){
		return party;
	}
	public int getNumVotes(){
		return numvotes;
	}
	public double getDollarsSpent(){
		return expenses;
	}
	public String getMotto(){
		return motto;
	}
 	//accessor methods above call to a candidate object INSIDE AN ARRAYLIST!!!
 	public void setCandidateName(String newname){
		name = newname;
	}
	public void setElectionOffice(String newelectionoffice){
		office = newelectionoffice;
	}
	public void setParty(char newparty){
		if (newparty == 'r' || newparty == 'd' || newparty == 'i' || newparty == 'o') {
			party = newparty;
		} else {
			System.out.println("You did not input a valid party. Please try again.");
		}
	}
	public void setNumVotes(int newnumvotes){
		if (newnumvotes>=0){
			numvotes = newnumvotes;
		} else {
			System.out.println("You did not input a valid amount of votes. Please try again.");
		}
	}
	public void setDollarsSpent(double newexpenses){
		if (newexpenses>=0){
			expenses = newexpenses;
		} else {
			System.out.println("You did not input a valid expenditure amount. Please try again.");
		}
	}
	public void setMotto(String newmotto){
		motto = newmotto;
	}
 	//mutator methods above call to a candidate object INSIDE AN ARRAYLIST!!!
/*	public void displayCandidate(Candidate candydate){
		System.out.println(candydate.getCandidateName());
		System.out.println(candydate.getElectionOffice());
		System.out.println(candydate.getParty());
		System.out.println(candydate.getNumVotes());
		System.out.println(candydate.getDollarsSpent());
		System.out.println(candydate.getMotto());
	}
	//Display method above
	public String candidateToString(){
		String foobar = name + " " + office + " " + party + " " + numvotes + " " + expenses + " " + motto;
		return foobar; //the variable name is not used anywhere else in the program. it is a dummy.
	}
	//toString Method above */
}