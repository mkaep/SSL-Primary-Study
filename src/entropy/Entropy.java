package entropy;

import org.deckfour.xes.model.XLog;
/**
 * Interface for different entropy measures. Measures are used to 
 * determine the information gain in an event log. This should enable 
 * to determine whether a reduced log works better than another reduced log
 * @author Martin Kaeppel
 */
public interface Entropy {
	public double calculate(XLog log);
}
