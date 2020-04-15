abstract class Vehicle {
	protected String vehicleType="";
	protected boolean passedTollBooth = false;
	protected int timeIn=0; 
	protected Integer timeOut=0;
	protected Integer timeAtFirstInLine;
	//set methods
	public void setFrontTime( int time ) { timeAtFirstInLine = time; }
	public void setType(String type){
		try {
			if (type.equalsIgnoreCase("M") || type.equalsIgnoreCase("A") ) {
				vehicleType = type;
			} else { throw new VehicleTypeException("Incorrect type. Must be A or M."); }
		} catch (VehicleTypeException e) { System.out.println("Object will not be used in this list.");}
	}
	public void setTimeIn(int time){ 
		try {
			if (time>0){
				timeIn = time;
			} else { throw new InvalidNumberException("Time is out of range.");}
		} catch (InvalidNumberException e) {System.out.println("Revise Input Values.");}
	}
	public void setTimeOut(int time){ 
		try {
			if (time>timeIn){
				timeOut = time;
			} else { throw new InvalidNumberException("Time is out of range.");}
		} catch (InvalidNumberException e) {System.out.println("Revise Input Values.");}
	}
	//get methods
	public String getType(){ return vehicleType; }
	public int getTimeIn(){ return timeIn; }
	public Integer getTimeOut(){ return timeOut; }
	public Integer getFrontTime(){ return timeAtFirstInLine; }
	public int getWaitingTime(){ return this.timeAtFirstInLine-timeIn; }
	public void passTollBooth(){ passedTollBooth = true; }
	//instanceOf method
	public boolean isManual() { if (this.getType().equalsIgnoreCase("M")) return true; else return false; }
	public boolean isAutomatic() { if (this.getType().equalsIgnoreCase("A")) return true; else return false; }
	//abstract methods
	abstract int getPassingTime();
	//constructor
	public Vehicle(int time, String type) {
		this.setTimeIn(time);
		this.setType(type);
		passedTollBooth = false; //this will never be true when an object is first instantiated
	}
	//public int compareTo(Vehicle that) { return that.getPassingTime() - this.getPassingTime(); } //allows for vehicle comparisons. THIS IS A TESTING METHOD & IS NEVER USED.
}