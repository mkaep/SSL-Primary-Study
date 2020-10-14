package ml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.Parser;

/**
 * This class splits and event log into test and trainingsdata
 * 
 * @author Martin Käppel
 *
 */
public class Splitter {
	
	public Splitter() {
		
	}
	
	public TestTrainObject splitEventLog(XLog log, double testSize) {
		Parser p = new Parser();
		
		int size = p.getNumberOfTraces(log);
		int numberOfTestElements = (int)(testSize*size);
		
		XFactory factory = new XFactoryBufferedImpl();
		XLog testLog = factory.createLog();
		XLog trainingLog = factory.createLog();
		
		List<String> caseIds = new ArrayList<String>(p.getCaseIds(log));
		List<String> testIds = new ArrayList<String>();
		
		while(numberOfTestElements > 0) {
			Collections.shuffle(caseIds);
			String id = caseIds.remove(0);
			numberOfTestElements--;
			testIds.add(id);
		}
				
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			if(testIds.contains(currentTrace.getAttributes().get(XConceptExtension.KEY_NAME).toString())) {
				testLog.add(currentTrace);
			}
			else {
				trainingLog.add(currentTrace);
			}
		}
		
		TestTrainObject trainTest = new TestTrainObject(trainingLog, testLog);
		
		return trainTest;
	}
	
	public TestTrainObject splitEventLogByTime(XLog log, double testSize) {
		TestTrainObject trainTest = new TestTrainObject(null, null);
		
		return trainTest;
	}
}
