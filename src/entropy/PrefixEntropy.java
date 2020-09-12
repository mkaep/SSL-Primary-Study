package entropy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.classifier.Prefix;
import parser.Parser;
/**
 * This class implements the interface Entropy and calculates the prefix entropy of an event log
 * 
 * @author Martin Kaeppel
 */
public class PrefixEntropy implements Entropy {
	
	public PrefixEntropy() {
		
	}
	
	public double calculate(XLog log) {
		double prefixEntropy = 0;
		Parser parser = new Parser();
		int numberOfEvents = parser.getNumberOfEvents(log);
				
		Set<Prefix> prefixes = parser.getPrefixes(log, false);
		
		Map<Prefix, Integer> map = new HashMap<Prefix, Integer>();
		for(Prefix pre : prefixes) {
			map.put(pre, 0);
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			Prefix p = new Prefix();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				
				String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
				p.append(activity);		
				
				
				Prefix nextPrefix = new Prefix();
				nextPrefix.setPrefix(new ArrayList<String>(p.getPrefix()));
				
				prefixes.add(nextPrefix);
				map.put(nextPrefix, map.get(nextPrefix)+1);
			}	
		}
		
		for(Prefix p : map.keySet()) {
			double prefixLikelihood = map.get(p)/(numberOfEvents*1.0);
			prefixEntropy = prefixEntropy+prefixLikelihood*Math.log(prefixLikelihood);
		}
				
		return prefixEntropy*(-1);
	}
	
	
	public double calculateWithLifefycle(XLog log, boolean lifecycle) {
		double prefixEntropy = 0;
		Parser parser = new Parser();
		int numberOfEvents = parser.getNumberOfEvents(log);
				
		Set<Prefix> prefixes = parser.getPrefixes(log, lifecycle);
		
		Map<Prefix, Integer> map = new HashMap<Prefix, Integer>();
		for(Prefix pre : prefixes) {
			map.put(pre, 0);
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			Prefix p = new Prefix();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				
				
				if(lifecycle == true) {
					String transition = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
					String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
					p.append(activity+"-"+transition);
				}
				else {
					String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
					p.append(activity);		
				}
				
				Prefix nextPrefix = new Prefix();
				nextPrefix.setPrefix(new ArrayList<String>(p.getPrefix()));
				
				prefixes.add(nextPrefix);
				map.put(nextPrefix, map.get(nextPrefix)+1);
			}	
		}
		
		for(Prefix p : map.keySet()) {
			double prefixLikelihood = map.get(p)/(numberOfEvents*1.0);
			prefixEntropy = prefixEntropy+prefixLikelihood*Math.log(prefixLikelihood);
		}
				
		return prefixEntropy*(-1);
	}
	
	

}
