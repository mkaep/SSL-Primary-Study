package reducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.Parser;

/**
 * This class provides different methods to reduce the event log
 * @author Martin Kaeppel
 */
public class ReducerRandom implements Reducer {
	
	public ReducerRandom() {
		
	}
	
	public XLog reduce(XLog log, double targetSize) {
		Parser p = new Parser();
		List<String> caseIds = new ArrayList<String>(p.getCaseIds(log));
		int numberToRemove = (int)(p.getNumberOfTraces(log)*(1-targetSize));
		while(numberToRemove > 0) {
			Collections.shuffle(caseIds);
			caseIds.remove(0);
			numberToRemove--;
		}
		
		XFactory factory = new XFactoryBufferedImpl();
		XLog reducedLog = factory.createLog();
		
		Set<String> reducedIds = new HashSet<String>(caseIds);
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			if(reducedIds.contains(XConceptExtension.instance().extractName(currentTrace))) {
				reducedLog.add(currentTrace);
			}
			
		}
		
		return reducedLog;
	}
}
