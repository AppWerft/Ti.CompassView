package ti.compassview;

import java.util.ArrayList;
import java.util.LinkedList;

public class Lowpass {
	double value = 0.0;
	int steps;
	LinkedList stack = new LinkedList();

	public void Lopass(int steps) {
		this.steps = steps;
	}

	public double addValue(double val) {
		this.stack.addLast(val);
		if (this.stack.size() > this.steps)
			this.stack.pollFirst();
		return value;

	}
}
