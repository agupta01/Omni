import java.util.ArrayList;

public class People {	

	// how many times the person survives
	public int values;
	public int trials;

	public double rate;

	public People() {
		this.values = 0;
		this.trials = 0;
		this.rate = 0.00;
	}

	// computes survival rate of person
	public void compRate() {
		this.rate = ((this.values + 0.00) / (this.trials + 0.00));
		//System.out.println(this.rate);
	}

}