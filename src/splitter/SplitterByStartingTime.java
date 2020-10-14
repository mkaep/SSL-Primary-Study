package splitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.Parser;


/**
 * This implementation of the Splitter Interface splits an event log along the 
 * time dimension. That means the traces that are first in time forms the training data and the
 * latest ones the test data.
 * 
 * @author 	Martin Käppel
 * @version	12.10.2020
 *
 */
public class SplitterByStartingTime implements Splitter {
	
	public SplitterByStartingTime() {
		
	}
	
	@Override
	public TestTrainObject splitEventLog(XLog log, double testSize) {
		List<TraceStartEntry> list = sortEventLog(log);
		
		Parser p = new Parser();
		int size = p.getNumberOfTraces(log);
		int numberOfTrainingElements = size - (int)(testSize*size);
				
		Set<String> trainingCases = new HashSet<String>();
		Set<String> testCases = new HashSet<String>();
		
		while(numberOfTrainingElements > 0) {
			trainingCases.add(list.remove(0).getCaseID());
			numberOfTrainingElements--;
		}
		
		for(TraceStartEntry e : list) {
			testCases.add(e.getCaseID());
		}
		
		XFactory factory = new XFactoryBufferedImpl();
		XLog testLog = factory.createLog();
		XLog trainingLog = factory.createLog();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			String currentCaseID = XConceptExtension.instance().extractName(currentTrace);
			if(trainingCases.contains(currentCaseID)) {
				trainingLog.add(currentTrace);
			}
			else {
				testLog.add(currentTrace);
			}
		}
		
		TestTrainObject trainTest = new TestTrainObject(trainingLog, testLog);

		return trainTest;
	}
	
	private List<TraceStartEntry> sortEventLog(XLog log) {
		List<TraceStartEntry> list = new ArrayList<TraceStartEntry>();
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			String caseID = XConceptExtension.instance().extractName(currentTrace);
			
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			//Annahme: Trace startet mit dem ersten Event (manchmal hat auch die Trace selber ein Start Timestamp, dies wird hier ignoriert. Wir beschraenken uns
			//auf den Timestamp des ersten Events.
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				Date startTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
				TraceStartEntry entry = new TraceStartEntry(caseID, startTimestamp);
				list.add(entry);
				break;
			}
		}
		
		Collections.sort(list, new Comparator<TraceStartEntry>() {

			@Override
			public int compare(TraceStartEntry o1, TraceStartEntry o2) {
				return o1.getStartTimestamp().compareTo(o2.getStartTimestamp());
			}
			
		});
		
		return list;
	}
	
	
	public class TraceStartEntry {
		private String caseID;
		private Date startTimestamp;
		
		public TraceStartEntry(String caseID, Date startTimestamp) {
			this.caseID = caseID;
			this.startTimestamp = startTimestamp;
		}
		
		public String getCaseID() {
			return caseID;
		}
		
		public Date getStartTimestamp() {
			return startTimestamp;
		}
		
		public String toString() {
			return caseID+": "+startTimestamp;
		}
	}

}
