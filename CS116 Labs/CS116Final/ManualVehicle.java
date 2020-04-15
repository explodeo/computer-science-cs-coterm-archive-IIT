public class ManualVehicle extends Vehicle {
	protected int wheelcount = 1;
	//constructor
	public ManualVehicle(int time, String type, int numOfWheels) {
		super(time,type);
		setPassingTime(numOfWheels);
	}
	//set Methods
	public void setPassingTime(int number){
		try {
			if (number>0){
				wheelcount = number;
			} else { throw new InvalidNumberException("Time is out of range.");}
		} catch (InvalidNumberException e) {System.out.println("Revise Input (number of wheels) Values.");}
	}
	//abstract method
	public int getPassingTime(){return wheelcount;}
	public String toString(){ return this.getTimeIn()+","+this.getType()+","+wheelcount; }
}