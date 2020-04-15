public class RandomData {
	public static int getInt(char type, int x, int y) {
		int num;
		switch (type) {
		case 'U': case 'u':
			num = (int)(x + (Math.random()*(y+1-x))); 
			break;
		case 'E': case 'e':
			num = (int)(-1*x*Math.log(Math.random()));  
			break;	
		case 'N': case 'n':			
			num = (int)( x +
                (y * Math.cos(2 * Math.PI * Math.random()) *
                Math.sqrt(-2 * Math.log(Math.random()))));
			break;			
		default:
			num = (int)(x + (Math.random()*(y+1-x))); 
		}
		return num;
	}
}