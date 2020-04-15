import java.util.*;
public class DoneVehicles {
	private List<ManualVehicle> manualList = new ArrayList<ManualVehicle>();
	private List<AutomaticVehicle> autoList = new ArrayList<AutomaticVehicle>();
	private int maxManualWait = 0;
	private int maxAutoWait = 0;
	private double avgManualWait = 0;
	private double avgAutoWait = 0;
	//Constructor
	public DoneVehicles(){}
	//access lists directly
	public List<ManualVehicle> getManualList() { return manualList; }
	public List<AutomaticVehicle> getAutomaticList() { return autoList; }
	//list info methods
	public int getMaxManualWait(){
		int maxWaitTime = 0, manualWait=0;//this is well above any possible waiting value
		for (ManualVehicle vehicle : manualList) {
			manualWait = vehicle.getWaitingTime();
			if(manualWait>maxWaitTime) { maxWaitTime = manualWait; }
		} return maxWaitTime;
	}
	public double getAverageManualWait(){
		double avgWaitTime = 0;
		for (ManualVehicle vehicle : manualList) { 
			avgWaitTime += (double)(vehicle.getWaitingTime())/(manualList.size()-1); //prevents overflow via += method
		} return avgWaitTime;
	}

	public int getMaxAutomaticWait(){
		int maxWaitTime = 0, autoWait=0;//this is well above any possible waiting value
		for (AutomaticVehicle vehicle : autoList) {
			autoWait = vehicle.getWaitingTime(); //System.out.println(vehicle + "\t" + vehicle.getWaitingTime());
			if(autoWait>maxWaitTime) { maxWaitTime = autoWait; }
		} return maxWaitTime;
	}
	public double getAverageAutomaticWait(){
		double totalWaitTime = 0, avgWaitTime = 0;
		for (AutomaticVehicle vehicle : autoList) {
			avgWaitTime += (double)(vehicle.getWaitingTime())/(autoList.size()-1); //prevents overflow
		} return avgWaitTime;
	}

	public int manualCount(){ return manualList.size()-1; }
	public int automaticCount(){ return autoList.size()-1; }

	//add to list methods by type
	public void addManual(ManualVehicle vehicle){ manualList.add(vehicle); vehicle.passTollBooth(); }
	public void addAutomatic(AutomaticVehicle vehicle){ autoList.add(vehicle); vehicle.passTollBooth(); }
}