import java.util.*; import java.io.*;
//JAVA VERSION 8 HAS ERRORS WITH FOR-EACH LOOPS IN FILE-SCANNING. EVERY LOOP IS A FOR LOOP
public class Simulator {
	//Constants
	private static final int MAX_LINE_LENGTH = 25;
	private static final int MAX_LINE_INDEX = 5;
	private static final int MAX_NUMOF_VEHICLES = 10000;
	public static void main(String[] args) throws IOException { //will break if file not found
		//IO Variables
		Scanner scan = new Scanner(System.in);
		System.out.print("Input File: ");
		File inFile = new File(scan.nextLine());
		int numManualBooths = 0, numAutomaticBooths = 0; 
		//handle min no. booths here
		while(numManualBooths<1||numManualBooths>6) {
			System.out.print("Manual Toll Booths: ");
			numManualBooths = scan.nextInt();
			if (numManualBooths<1||numManualBooths>6) System.out.println("\n\nInvalic number of tollbooths. Please enter at least 1.");
		}
		scan.close(); //precents resource leaks by scanner
		numAutomaticBooths = MAX_LINE_INDEX+1 - numManualBooths; 
		System.out.println("Automatic Toll Booths: "+ numAutomaticBooths+"\n");
		//create tollBooth collection here
		TollBoothLine [] tollBooths = new TollBoothLine[MAX_LINE_INDEX+1];				//create array sized only to what is needed
		int lineindex = 0;
		for (int x=0;x<=MAX_LINE_INDEX;x++){ //sets first [x] values in array as manual tollBooths
			if (lineindex<numManualBooths) { tollBooths[lineindex] = new TollBoothLine("M"); } 
			else { tollBooths[lineindex] = new TollBoothLine("A"); } 
			lineindex++;
		} 

		Vehicle [] vehicles = fileToArray(inFile).clone(); //deep-copies object array to avoid data loss
		for (int vv = 0; vv<vehicles.length-1; vv++){ System.out.println(vehicles[vv]); }
		DoneVehicles doneList = new DoneVehicles();
		//Simulation 
		int index = 0; //vehicle or line indexing variable
		int count = 1; //used to start at a certain index to minimize time comparisons for adding vehicles to tollbooths
		Integer time = -1; //time is incremented at the beginning, so time need to be -1 to start at time = 0
		boolean done = false; //timer and stopper
		while(!done){
			time++; //starts time at 1
			for (index = count; index<vehicles.length-1; index++ ) {
				if (vehicles[index].getTimeIn()==time) { //adds vehicles to shortest lines when time=VehicleInTime
					addToShortestLine(vehicles[index], tollBooths); 
					count++; 
				} 
				if (vehicles[index].getTimeIn() > time) { break; } //minimizes number of comparisons in the list if the loop breaks when the desired item is found. Worst case is N comparisons
			}
			for (index = 0; index<tollBooths.length-1; index++) { //sets time when vehicle gets to front of line
				if (tollBooths[index].getLineLength()>0) //handles empty tollBooth lines
					if (tollBooths[index].getQueue(0).getFrontTime()==null) { tollBooths[index].getQueue(0).setFrontTime(time); } //null error does not need handling. all vehicles will have Integer reassigned
			}
			for (index = 0; index<tollBooths.length-1; index++) {
				if (tollBooths[index].getLineLength()>0) { //handles empty tollboothlines
					if (time == tollBooths[index].getQueue(0).getFrontTime()+tollBooths[index].getQueue(0).getPassingTime()) { //if vehicle is front of the line and its passing time is called
						if(tollBooths[index].getQueue(0).isManual()) { doneList.addManual((ManualVehicle)tollBooths[index].next(time)); } //tollBooths[index].next() is a vehicle object that is returned. Object is converted into a subclass object
						else { doneList.addAutomatic((AutomaticVehicle)tollBooths[index].next(time)); }
					}
				}
			}
			if (count == vehicles.length-1) done = true;
		}

		//Display doneList stats to console below
		int linecount = 0;
		for (linecount=0;linecount<numManualBooths;linecount++) { System.out.println("Manual Line #" + (linecount+1) + " Maximum Length=" + tollBooths[linecount].getMaxLengthReached()); }
		for (linecount=numManualBooths;linecount<=MAX_LINE_INDEX;linecount++) { System.out.println("Automatic Line #" + (linecount-numManualBooths+1) + " Maximum Length=" + tollBooths[linecount].getMaxLengthReached()); }

		System.out.println("Max Manual Wait = " + doneList.getMaxManualWait());
		System.out.println("Max Automatic Wait = " + doneList.getMaxAutomaticWait());
		System.out.println("Avg Manual Wait = " + doneList.getAverageManualWait());
		System.out.println("Avg Auto Wait = " + doneList.getAverageAutomaticWait());
	}

	//FILE-SCANNING METHOD
	public static Vehicle[] fileToArray(File file) throws IOException { //errors will be thrown if parameters in file are incorrect.
		StringTokenizer line; final String delim = ","; String temp = ""; boolean done = false;
		Scanner scan = new Scanner(file);
		Vehicle [] fileArray = new Vehicle[MAX_NUMOF_VEHICLES];
		int count=1;
		while(scan.hasNextLine()) {
			temp = scan.nextLine(); 
			line = new StringTokenizer(temp,delim,false);//tokenize string without commas for better data
			if (line.countTokens() == 2){ //tokenizer can be called twice for auto vehicles
				fileArray[count] = new AutomaticVehicle(Integer.parseInt(line.nextToken()),line.nextToken()); //create automatic Vehicle
			} else if (line.countTokens() == 3) {
				fileArray[count] = new ManualVehicle(Integer.parseInt(line.nextToken()),line.nextToken(),Integer.parseInt(line.nextToken())); //create manual vehicle
			} count++;
		}
		Vehicle [] arr = Arrays.<Vehicle>copyOf(fileArray,count); //truncates nulls from array here
		scan.close(); //prevents resource leaks
		return arr;
	}

	//APPENDING METHODS
	public static void addToShortestLine(Vehicle vehicle, TollBoothLine [] tollBooths){ 
		int minLineLength = 0, lineLength = 0, index = 0, minLocation = 0;
		
		int x=0; //vehicle or line indexing variable
		for (x=0; x<MAX_LINE_INDEX+1;x++) { //checks all TollBoothLines in the collection
			if (vehicle.getType().equalsIgnoreCase(tollBooths[x].getLineType())){ //if vehicle and line are same type
				index = (index>=tollBooths[x].getLineLength()) ? x:index; //find shortest line of vehicle type
			}
		}
		tollBooths[index].addVehicle(vehicle); //add vehicle to shortest line
/**

	} 
		int minLineLength = 0, lineLength = 0, index = 0, minLocation = 0;
		for (index=0; index<=MAX_LINE_INDEX;index++) {
			if (vehicle.getType().equalsIgnoreCase(tollBooths[index].getLineType())){ //if vehicle and line are same type
				lineLength = tollBooths[index].getLineLength(); //assigns a comparable value
				if(lineLength<minLineLength) { minLineLength = lineLength; System.out.println(minLineLength +"  "+ minLocation); } // compares lengths and stores location and length
				minLocation = index;
			}
		} tollBooths[minLocation].addVehicle(vehicle); //add vehicle to shortest line
		System.out.println(minLocation+ "  "+ vehicle);
*/
	}
}