package entropy;

import java.util.Map;

import org.deckfour.xes.model.XLog;

import parser.Parser;
import parser.TraceVariant;

/**
 * This class implements the interface Entropy and calculates the Shannon Entropy of an event log
 * @author Martin Kaeppel
 */

public class ShannonEntropy implements Entropy {
	
	public ShannonEntropy() {
		
	}
	
	public double calculate(XLog log) {
		double shannonIndex = 0;
		
		Parser p = new Parser();
		double individuals = p.getNumberOfTraces(log);
		
		Map<TraceVariant, Integer> map = p.getCountedTraceVariants(log);
		for(TraceVariant variant : map.keySet()) {
			shannonIndex = shannonIndex+((map.get(variant)/individuals)*Math.log(map.get(variant)/individuals));
		}
				
		return shannonIndex*(-1);
	}
	
	public double getMaximalShannonIndex(XLog log) {
		Parser p = new Parser();
		return Math.log(p.getTraceVariants(log).size());
	}

}
