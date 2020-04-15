import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class TollSimulatorFileGenerator {
	public static void main(String[] args) {
		try  {
			final int AUTOMATIC_COUNT=7;  // 7 out of 10 vehicles are automatic lanes
			final int ARRIVAL_MEAN=10;   // 4 for rush hour, 10 for non rush hour
			final int AXEL_MEAN=2;
			int simulationLength=7200, minute=0, nextArrivalTime; 
			nextArrivalTime = minute + RandomData.getInt('E', ARRIVAL_MEAN, 0);
	
			FileWriter fw = new FileWriter( "output.txt", false);
			BufferedWriter bw = new BufferedWriter( fw );
	   
			while(minute<=simulationLength) {
				while ((minute == nextArrivalTime) && (minute<=simulationLength)) {
					// determine if automatic toll vehicle or manual toll vehicle
					if (RandomData.getInt('U', 1, 10)<=AUTOMATIC_COUNT) { 
						bw.write(minute+",A");
						bw.newLine( );				
					}
					else {
						// determine number of wheels
						int wheels = 4+2*RandomData.getInt('E', AXEL_MEAN, 0);
						while (wheels>18) 
							wheels = 4+2*RandomData.getInt('E', AXEL_MEAN, 0);
						bw.write(minute+",M,"+wheels);
						bw.newLine( );		
					}
					nextArrivalTime=minute+RandomData.getInt('E', ARRIVAL_MEAN, 0);
				}
				minute++;
			} 
			bw.close( );
		}
	    catch( IOException ioe ) {
			ioe.printStackTrace( );
		}
	}
}