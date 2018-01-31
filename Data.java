import java.util.ArrayList;

public class Data {

	public int kill;
	public ArrayList<Integer> victimLog = new ArrayList<Integer>();

	public Data() {
		this.kill = -1;
	}

	public double sum() {
		int sum = 0;
		for (int a = 0; a < this.victimLog.size(); a++) {
			sum += victimLog.get(a);
		}

		return sum;
	}

	public int logSize() {
		return victimLog.size();
	}

}