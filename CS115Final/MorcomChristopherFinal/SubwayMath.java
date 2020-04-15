import java.text.DecimalFormat;
public class SubwayMath {
	final DecimalFormat PERCENTFORMAT = new DecimalFormat("##0.0%");
	final DecimalFormat DOLLARFORMAT = new DecimalFormat("$##0.00");
	final double DEFAULT_BONUS_PERCENT = 15.00;
	final double DEFAULT_RIDECOST = 2.00;	//constants
	private double percentbonus;
	private double ridecost;	//instance vars
	public SubwayMath() {	//default constructor
		ridecost = DEFAULT_RIDECOST;
		percentbonus = DEFAULT_BONUS_PERCENT;
	}
	public SubwayMath(double p, double r) { //user-instantiated constrcutor
		ridecost = r; percentbonus = p;
	}
	//ACCESSOR METHODS
	public double getRideCost() {
		return ridecost;
	}
	public double getPercentBonus() {
		return percentbonus;
	}
	//MUTATOR METHODS
	public void setRideCost(double r) {
		ridecost = r; 
	}
	public void setPercentBonus(double p) {
		percentbonus = p;
	}
	//OTHER METHODS
	public String toString() {
		return "Bonus: "+ PERCENTFORMAT.format(percentbonus) + " Ride Cost: "+ DOLLARFORMAT.format(ridecost);
	}
	public String optimalAmount(int count) {
		double totalcost = count*ridecost;
		double optimalamt = totalcost/(1.00 + percentbonus); //optimalAMT = total cost/1XX.XX% bonus
		return DOLLARFORMAT.format(optimalamt);
	}
}