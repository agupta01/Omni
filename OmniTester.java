import java.util.*;

public class OmniTester {
	public static void main(String[] args) {
		Omni omni = new Omni();
		Scanner console = new Scanner(System.in);
		switch(console.nextInt()) {
			case 1:
				reg(console, omni);
				break;
			case 2:
				test(console, omni);
				break;
			default:
				System.out.println("error");
				System.exit(0);
		}
	}

	public static void reg(Scanner console, Omni omni) {
		// setup people profiles
		for (int i = 0; i < 10; i++) {
			omni.people[i] = new People();
		}

		// setup temp data holder
		for (int z = 0; z < 100; z++) {
			omni.holder[z] = new Data();
		}
		System.out.println("DISCLAIMER: This program is not designed to replicate real "
						 + "driving situations and driver/computer actions. It is simply a "
						 + "rudimentary demonstration of supervised machine learning. As "
						 + "such, use this program to explore how you might react to a "
						 + "simplified example of a moral delimma and how a computer may "
						 + "detect and adjust to your personal preferences. If you believe "
						 + "you may have an averse reaction to this program, please close it.\n"
						 + "Thank you.");
		System.out.println("This is the situation: You are driving on a straight road near a cliff, "
							+ "when all of a sudden, a sharp curve appears. You are going too fast "
							+ "to brake, which means you have two options:\n"
							+ " y - swerve into a group of pedestrians on the sidewalk, killing them.\n"
							+ " n - keep going straight, sacrificing yourself.");

		omni.calibration(console);
		System.out.println(Arrays.toString(omni.rates) + "\n" + Arrays.toString(omni.numRates));

		boolean fin = false;
		while (fin == false) {
			omni.round2(console);
			System.out.print("Finish? (y/n): ");
			String a = console.next();
			if (a.equalsIgnoreCase("y"))
				fin = true;
		}
	}

	public static void test(Scanner console, Omni omni) {
		// setup people profiles
		for (int i = 0; i < 10; i++) {
			omni.people[i] = new People();
		}

		// setup temp data holder
		for (int z = 0; z < 100; z++) {
			omni.holder[z] = new Data();
		}
		System.out.println("DISCLAIMER: This program is not designed to replicate real "
						 + "driving situations and driver/computer actions. It is simply a "
						 + "rudimentary demonstration of supervised machine learning. As "
						 + "such, use this program to explore how you might react to a "
						 + "simplified example of a moral delimma and how a computer may "
						 + "detect and adjust to your personal preferences. If you believe "
						 + "you may have an averse reaction to this program, please close it.\n"
						 + "Thank you.");
		System.out.println("This is the situation: You are driving on a straight road near a cliff, "
							+ "when all of a sudden, a sharp curve appears. You are going too fast "
							+ "to brake, which means you have two options:\n"
							+ " y - swerve into a group of pedestrians on the sidewalk, killing them.\n"
							+ " n - keep going straight, sacrificing yourself.");

		omni.calibration(console);
		System.out.println(Arrays.toString(omni.rates) + "\n" + Arrays.toString(omni.numRates));
	}
}