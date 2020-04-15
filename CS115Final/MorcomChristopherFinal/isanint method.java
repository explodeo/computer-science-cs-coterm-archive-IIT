import java.util.Scanner;
public class delthis {
	public static void main(String [] args) {
		int x = 0;
		String s;
		Scanner scan = new Scanner(System.in);
		System.out.println("print a line");
		s = scan.nextLine();

		if (s < 50){
			System.out.println("is an int");
		} else {
			System.out.println("not an int");
		}
	}
}