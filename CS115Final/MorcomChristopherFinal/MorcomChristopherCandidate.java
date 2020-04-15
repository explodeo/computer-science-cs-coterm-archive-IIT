/* Christopher Morcom
 * A20385764
 * CS115-02 */

/*	This program creates a new object for use in the MorcomChristopher.java file. 
	It contains methods including accessor and mutator methods to get or edit values inside the Object.
	It has methods to display the Object or compare the Object to another Object.
	There is also a method to make the Object into a String Object.
*/
public class MorcomChristopherCandidate {

	public String name;
	private String office;
	private char party;
	private int numvotes;
	private double expenses; 
	private String motto;
	private String state;
	
	public MorcomChristopherCandidate(String n, String ofce, char p, int nv, double e, String m, String st) {
		name = n;
		office = ofce;
		party = p;
		numvotes =nv;
		expenses = e;
		motto = m;
		state = st;
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
	public String getState(){
		return state;
	}
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
	public void setState(String newstate){
		state = newstate;
	}
	public void displayCandidate(){
		System.out.println("Name (State): " + name + " ("+state+")");
		System.out.println("Election office: " + office);
		if (party == 'r') {
			System.out.println("Political party: Republican");
		} else if (party == 'd') {
			System.out.println("Political party: Democrat");
		} else if (party == 'i') {
			System.out.println("Political party: Independent");
		} else if (party == 'i') {
			System.out.println("Political party: Republican");
		}
		System.out.println("Votes Recieved: " + numvotes);
		System.out.println("Total Expenses: " + expenses);
		System.out.println("Motto: " + motto);
	}
	public String candidateToString(){
		String tostringcandidate = "("+ state +") "+ name + " " + office + " " + party + " " + numvotes + " " + expenses + " " + motto;
		return tostringcandidate; 
	}
	public Boolean candidateEquals(MorcomChristopherCandidate candidate1) {
		
		if (name == candidate1.getCandidateName() && office == candidate1.getElectionOffice() && 
			party == candidate1.getParty() && state == candidate1.getState() && motto == candidate1.getMotto() && //checks each field to see if they are equal
			numvotes == candidate1.getNumVotes() && expenses == candidate1.getDollarsSpent()) {
			return true;
		} else {
			return false;
		}
	} 
}