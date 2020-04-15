public class AutomaticVehicle extends Vehicle {
	protected final int passingTime = 1;
	//constructor
	public AutomaticVehicle(int time, String type) { super(time,type); }
	//abstract method
	public int getPassingTime(){return passingTime;}
	public String toString(){ return this.getTimeIn()+","+this.getType(); }
}