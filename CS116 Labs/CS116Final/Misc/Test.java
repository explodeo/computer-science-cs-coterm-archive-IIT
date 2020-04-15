import java.util.*;
import java.io.*;
public class Test {
	public static void main(String[] args) throws IOException{
	//	TollBoothLine line = new TollBoothLine("M");
	//	System.out.println(line.getLineLength());
	Vehicle []vlist = new Vehicle[2]; vlist[0] = new ManualVehicle(1,"M",4); 
	vlist[1] = new AutomaticVehicle(2,"A");
	System.out.println(vlist[0].getTimeIn());
	System.out.println(vlist[1].getTimeIn());
	}
}