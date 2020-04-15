import java.util.*; import java.io.*;
//JAVA VERSION 8 HAS ERRORS WITH FOR-EACH LOOPS IN FILE-SCANNING. EVERY LOOP IS A FOR LOOP
public class SimulatorBackup {
	//Constants
	private static final int MAX_LINE_LENGTH = 25;
	private static final int MAX_LINE_INDEX = 5;
	private static final int MAX_NUMOF_VEHICLES = 50000;
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
		System.out.println("Automatic Toll Booths: "+ numAutomaticBooths);
		System.out.println("");
		//create tollBooth collection here
		TollBoothLine [] tollBooths = new TollBoothLine[MAX_LINE_INDEX+1];				//create array sized only to what is needed
		//System.out.println("DEBUG: tollbooths = " + tollBooths.length);
		int lineindex = 0;
		for (int x=0;x<=MAX_LINE_INDEX;x++){ //sets first [x] values in array as manual tollBooths
			if (lineindex<numManualBooths) { tollBooths[lineindex] = new TollBoothLine("M"); } 
			else { tollBooths[lineindex] = new TollBoothLine("A"); } 
			lineindex++;
		} 
		Vehicle [] vehicles = fileToArray(inFile).clone(); //deep-copies object array to avoid data loss
		DoneVehicles doneList = new DoneVehicles();
		//Simulation 
		int index = 0; //vehicle or line indexing variable
		int count = 1; //used to start at a certain index to minimize time comparisons for adding vehicles to tollbooths
		Integer time = -1; //time is incremented at the beginning, so time need to be -1 to start at time = 0
		boolean done = false; //timer and stopper
		while(!done){
			time++; //starts time at 0
			for (index = count; index<vehicles.length; index++ ) {	//adds vehicles to shortest lines when time=VehicleInTime
				if (vehicles[index].getTimeIn()==time) { addToShortestLine(vehicles[index], tollBooths); count++; } 
				if (vehicles[index].getTimeIn() > time) { break; } //minimizes number of comparisons in the list if the loop breaks when the desired item is found. Worst case is N comparisons
			}
			for (index = 0; index<=MAX_LINE_INDEX; index++) { //sets time when vehicle gets to front of line
				if (tollBooths[index].getLineLength()>0) //handles empty tollBooth lines. 
					if (tollBooths[index].getQueue(0).getFrontTime()==null) { tollBooths[index].getQueue(0).setFrontTime(time); } //assigns or prevents reassigning of the front time for the first vehicle in line
			}
			for (index = 0; index<=MAX_LINE_INDEX; index++) { //checks and moves vehicles 
				if (tollBooths[index].getLineLength()>0) { //handles empty tollboothlines
					// If there are vehicles in the tollbooth[index], then check if the time currently is equal to the front time + passing time
					if (time == tollBooths[index].getQueue(0).getFrontTime()+tollBooths[index].getQueue(0).getPassingTime()) { 
						//if vehicle is front of the line and its passing time is called, then 
						if(tollBooths[index].getQueue(0).isManual()) { doneList.addManual((ManualVehicle)tollBooths[index].next(time)); } //tollBooths[index].next() is a vehicle object that is returned. Object is converted into a subclass object
						else { doneList.addAutomatic((AutomaticVehicle)tollBooths[index].next(time)); }
					}
				}
			}
			if (count == vehicles.length) done = true; // once going through all the vehicles, then done.
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
		int count=0;
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
		int minLineLength = 0, lineLength = 0, index = 0, minLocation = 0; //vehicle or line indexing variables
		for (index=0; index<=MAX_LINE_INDEX;index++) { //checks all TollBoothLines in the collection
			if (vehicle.getType().equalsIgnoreCase(tollBooths[index].getLineType())){ //if vehicle and line are same type
				minLocation = (tollBooths[minLocation].getLineLength()>=tollBooths[index].getLineLength()) ? index:minLocation; //find shortest line of vehicle type
			}
		}
		tollBooths[minLocation].addVehicle(vehicle); //add vehicle to shortest line
	}
}