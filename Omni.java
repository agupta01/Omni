// Also other factors:
//		- Type of car
//		- Race
//		- Number of ppl (prediction)
import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

//NOTE: volume changes for round 2, becomes k-1

public class Omni {

	// number of inputs per person type to be calibrated
	public static final int CALIBRATION_CONSTANT = 5;
	// difference between nS and s averages to assume causation
	public static final double TRUTH_CONSTANT = 0.2;
	// max # of people on sidewalk
	public static final int VOLUME = 3;

	/*  0: male infant
		1: female infant
		2: male teen
		3: female teen
		4: dog
		5: pregnant woman
		6: male adult
		7: female adult
		8: male senior
		9: female senior
	*/
	public static People[] people = new People[10];
	public static double[] rates = new double[10];
	// survival rate of self for different population sizes
	public static double[] numRates = new double[VOLUME];
	public static Data[] holder = new Data[100];
	public static Data[] data;

	public static double nS;
	public static double nSTrials = 0.00;
	public static double s;
	public static double sTrials = 0.00;
	public static double selfRate = 0.00;
	public static double threshold;
	public static boolean direction = true;
	public static int volumeHolder;

	public static ArrayList<Integer> victimList = new ArrayList<Integer>();

	public static void main(String[] args) {
		Scanner console = new Scanner(System.in);
		// setup people profiles
		for (int i = 0; i < 10; i++) {
			people[i] = new People();
		}

		// setup temp data holder
		for (int z = 0; z < 100; z++) {
			holder[z] = new Data();
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

		calibration(console);
		System.out.println(Arrays.toString(rates) + "\nNon-swerve: " + nS + "\nSwerve: " + s + "\nThreshold: " + threshold + "\n\n");

		boolean fin = false;
		while (fin == false) {
			round2(console);
			System.out.print("Finish? (y/n): ");
			String a = console.next();
			if (a.equalsIgnoreCase("y"))
				fin = true;
		}

	}
	public static void calibration(Scanner console) {
		System.out.println("-------Beginning calibration------");
		int done = 0;
		int dCount = 0;
		boolean manual = false;

		while (done < 10) {

			done = 0;

			// generate random situation
			situation(dCount, VOLUME);

			// variables needed for choice robustness
			String c = "a";
			boolean end = false;

			while (end == false) {
				System.out.print("Swerve? (y/n): ");
				c = console.next();
				switch (c) {
					case "y":
						System.out.println("You have chosen to sacrifice the pedestrians.");
						log(c);
						holder[dCount].kill = 1;
						sTrials++;
						end = true;
						break;
					case "n":
						System.out.println("You have chosen to sacrifice yourself.");
						log(c);
						holder[dCount].kill = 0;
						nSTrials++;
						end = true;
						break;
					case "manual":
						end = true;
						manual = true;
						manualEntry(console);
						break;
					default:
						System.out.println("You did not enter a valid choice.");
						break;
				}
			}

			for (int i = 0; i < 10; i++) {
				if (people[i].trials >= CALIBRATION_CONSTANT) {
					done++;
				}
				if (manual == true)
					done = 11;
			}

			//if (nS < 3 || s < 3)
			//	done = 0;

			/*for(People x: people)
				System.out.printf(" %3d ", x.values);
			System.out.println();
			for(People x: people)
				System.out.printf(" %3d ", x.trials);
				*/

			dCount++;
			victimList.clear();

		}
		System.out.println("\n-------Calibration Finished-------");

		if (manual == false) {
			calculate();
			selfRate = (sTrials + 0.00) / ((sTrials + nSTrials) + 0.00);
		}
		System.out.println("Swerved:   " + sTrials);
		System.out.println("No-Swerve: " + nSTrials);
		System.out.println("Self Rate: " + selfRate);
		

		trim();
	}

	public static void manualEntry(Scanner console) {
		System.out.println("You have opted to enter data manually: Enter a number 0-1 for each query below.");
		System.out.print("Self: ");
		selfRate = console.nextDouble();
		for (int i = 0; i < rates.length; i++) {
			switch (i) {
				case 0:
					System.out.print("Male infant: ");
					break;
				case 1:
					System.out.print("Female infant: ");
					break;
				case 2:
					System.out.print("Male teen: ");
					break;
				case 3:
					System.out.print("Female teen: ");
					break;
				case 4:
					System.out.print("Dog: ");
					break;
				case 5:
					System.out.print("Pregnant woman: ");
					break;
				case 6:
					System.out.print("Male adult: ");
					break;
				case 7:
					System.out.print("Female adult: ");
					break;
				case 8:
					System.out.print("Male senior citizen: ");
					break;
				case 9:
					System.out.print("Female senior citizen: ");
			}
			rates[i] = console.nextDouble();
			//System.out.println(Arrays.toString(rates));
		}

		System.out.println("Enter values for different population sizes:");
		for (int i = 0; i < numRates.length; i++) {
			System.out.print(i + 1 + ": ");
			numRates[i] = console.nextDouble();
		}
	}

	public static void trim() {
		int size= 0;
		// find # of non-null entries
		for (int p = 0; p < 100; p++) {
			if (holder[p].kill != -1) {
				size++;
			}
		}

		// copy over data to trimmed array
		data = new Data[size];
		for (int a = 0; a < size; a++) {
			data[a] = new Data();
			data[a].kill = holder[a].kill;
			data[a].victimLog = holder[a].victimLog;
		}

	}

	public static void situation(int dCount, int volume) {
		System.out.println("\n\nIn the car:\n"
							+ " - You");

		// generates number of people on sidewalk
		Random random = new Random();
		volumeHolder = random.nextInt(volume) + 1;

		// Lists out people on sidewalk
		System.out.println("On the sidewalk:");
		for (int x = 0; x < volumeHolder; x++) {
			boolean cal = false;
			while(cal == false) {
				int pick = random.nextInt(10);
				if (people[pick].trials < CALIBRATION_CONSTANT) {
					victimList.add(pick);
					holder[dCount].victimLog.add(pick);
					cal = true;
				}
			}
			switch (victimList.get(x)) {
				case 0:
					System.out.println(" - Male infant");
					break;
				case 1:
					System.out.println(" - Female infant");
					break;
				case 2:
					System.out.println(" - Male teen");
					break;
				case 3:
					System.out.println(" - Female teen");
					break;
				case 4:
					System.out.println(" - Dog");
					break;
				case 5:
					System.out.println(" - Pregnant woman");
					break;
				case 6:
					System.out.println(" - Male adult");
					break;
				case 7:
					System.out.println(" - Female adult");
					break;
				case 8:
					System.out.println(" - Male senior citizen");
					break;
				case 9:
					System.out.println(" - Female senior citizen");
			}
		}
		//System.out.println(victimList.toString());
	}

	// logs values on all people
	public static void log(String c) {
		for (int a = 0; a < victimList.size(); a++) {
			if (c.equalsIgnoreCase("n")) {
				people[victimList.get(a)].values++;
				numRates[volumeHolder]++;
			}
			people[victimList.get(a)].trials++;
		}
	}

	// finds rate, decision threshold, and direction
	public static void calculate() {
		//compile rates
		for (int i = 0; i < 10; i++) {
			people[i].compRate();
			rates[i] = people[i].rate;
		}

		//find averages for non-swerve and swerve values (avg each)
		for (int i = 0; i < data.length; i++) {	//scrolls through all situations
			//DEBUG: System.out.println(data[i].logSize() +" " + holder[i].logSize());
			if (data[i].kill == 1) {	// if situation is a fatality
				for (int x = 0; x < holder[i].logSize(); x++) {	//scrolls thorugh situation's victims to find total sum
					s += people[holder[i].victimLog.get(x)].rate;
				}
			} else if (data[i].kill == 0) { // if situation is not a fatality
				for (int x = 0; x < holder[i].logSize(); x++) {	//scrolls thorugh situation's victims to find total sum
					nS += people[holder[i].victimLog.get(x)].rate;
				}
			}
		}


		nS = (nS + 0.00) / (nSTrials + 0.00);
		s = (s + 0.00) / (sTrials + 0.00);

		//find threshold (avg n-s and s)
		threshold = (nS + s) / 2.00;

		// check direction, if s <= n-s then true, otherwise false
		if (s > nS) {
			direction = false;
			System.out.println("It appears you tend to swerve into pedestrians when more of them"
								+ " are present. If this is false, you may want to restart the"
								+ " program. If this is true, please see a therapist.");
			System.exit(0);
		}

		// check truth
		if (Math.abs(nS - s) < TRUTH_CONSTANT) {
			System.out.println("Sorry, but there appears to be minimal correlation between your "
								+ "decisions to swerve into different groups of people. Please "
								+ "restart the program.");
			System.exit(0);
		}
	}

	// same thing as calibration, but with prediction and no logging
	public static void round2(Scanner console) {
		// reset trials to re-enable all people types
		for (int a = 0; a < people.length; a++) {
			people[a].trials = -999;
		}

		situation(99, VOLUME - 1);	//index is 99 b/c it is designated holder dataset
		boolean end = false;
		String c = "a";
		while (end == false) {
			System.out.print("Swerve? (y/n): ");
			c = console.next();
			switch (c) {
				case "y":
					System.out.println("You have chosen to sacrifice the pedestrians.");
					end = true;
					break;
				case "n":
					System.out.println("You have chosen to sacrifice yourself.");
					end = true;
					break;
				default:
					System.out.println("You did not enter a valid choice.");
					break;
			}
			//System.out.println(c);
		}

		System.out.println("Omni Predicted:\t\t\t\t" + prediction());
		System.out.println("Mean:\t\t\t\t          " + mean);

		/*if (prediction().equals(Omar())) {
			System.out.println("Otis Predicted:\t\t\t\t" + Omar());
		} else {
			if (Math.abs(totalVal - threshold) > Math.abs(mean - 0.5)) {
				System.out.println("Otis Predicted:\t\t\t\t" + prediction());
			} else {
				System.out.println("Otis Predicted:\t\t\t\t" + Omar());
			}
		}*/

		victimList.clear();
		holder[99].victimLog.clear();
		totalVal = 0.00;
		mean = 0.00;

	}

	// predicts user's input using totalValue algo
	// @returns: "SWERVE" or "NO SWERVE"
	static double totalVal = 0.00;
	/*
	public static String prediction() {
		for (int i = 0; i < victimList.size(); i++) {
			totalVal += people[victimList.get(i)].rate;
		}

		if (totalVal < threshold) {
			return "SWERVE";
		}

		return "NO SWERVE";
	}*/

	// algo using values as survival probabilities
	static double mean = 0.00;
	public static String prediction() {

		for (int i = 0; i < victimList.size(); i++) {
			mean += people[victimList.get(i)].rate;
		}
		mean /= (victimList.size() + 0.00);
		double selfRate = 0.5;
		// DEPRECATED: double selfRate = (sTrials + 0.00) / ((sTrials + nSTrials) + 0.00);

		if (mean <= selfRate) {
			return "SWERVE";
		}

		return "NO SWERVE";
	}

}
