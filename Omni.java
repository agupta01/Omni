/** Also other factors:
*		- Type of car
* 		- Race
*		- Number of ppl (prediction)
 */
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
	public People[] people;
	public double[] rates;
	// survival rate of self for different population sizes
	public double[] numRates;
	public Data[] holder;
	public Data[] data;

	public double nS;
	public double nSTrials ;
	public double s;
	public double sTrials;
	public double selfRate;
	public double threshold;
	public boolean direction;
	public int volumeHolder;

	public ArrayList<Integer> victimList;

	public Omni() {
		people = new People[10];
		rates = new double[10];
		numRates = new double[VOLUME];
		holder = new Data[100];
		nSTrials = 0.00;
		sTrials = 0.00;
		selfRate = 0.00;
		direction = true;
		victimList = new ArrayList<Integer>();
	}

	public void calibration(Scanner console) {
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
			trim();
			calculate();
			selfRate = (sTrials + 0.00) / ((sTrials + nSTrials) + 0.00);
		}
		System.out.println("Swerved:   " + sTrials);
		System.out.println("No-Swerve: " + nSTrials);
		System.out.println("Self Rate: " + selfRate);
		

		
	}

	public void manualEntry(Scanner console) {
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

	public void trim() {
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

	public void situation(int dCount, int volume) {
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
	public void log(String c) {
		for (int a = 0; a < victimList.size(); a++) {
			if (c.equalsIgnoreCase("n")) {
				people[victimList.get(a)].values++;
				numRates[volumeHolder - 1]++;
			}
			people[victimList.get(a)].trials++;
		}
	}

	// finds rate, decision threshold, and direction
	public void calculate() {
		//compile rates
		for (int i = 0; i < 10; i++) {
			people[i].compRate();
			rates[i] = people[i].rate;
		}

		//for (int i = 0; i < numRates.length; i++) {
		//	numRates[i] /= (nSTrials + sTrials);
		//}

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
	public void round2(Scanner console) {
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

		//System.out.println("Omni Predicted: \t\t\t\t" + prediction());
		System.out.println("Omni2 Predicted:\t\t\t\t" + prediction2());

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

	public String prediction() {

		for (int i = 0; i < victimList.size(); i++) {
			mean += people[victimList.get(i)].rate;
		}
		mean /= (victimList.size() + 0.00);
		//double selfRate = 0.5;
		// DEPRECATED: double selfRate = (sTrials + 0.00) / ((sTrials + nSTrials) + 0.00);

		if (mean <= selfRate)
			return "SWERVE";

		return "NO SWERVE";
	}

	public String prediction2() {
		mean = 0.00;

		for (int i = 0; i < victimList.size(); i++) {
			System.out.println(Arrays.toString(numRates));
			mean += people[victimList.get(i)].rate;
			System.out.println(people[victimList.get(i)].rate);
			if (people[victimList.get(i)].rate == 1.0 || people[victimList.get(i)].rate == 1)
				return "NO SWERVE";
			System.out.println("Person " + (i+1) + " checked!");
		}
		mean /= (victimList.size() + 0.00);

		double crit = numRates[volumeHolder - 1];

		if (mean <= crit)
			return "SWERVE";

		return "NO SWERVE";
	}

}
