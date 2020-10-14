package preprocessing;

import java.util.Date;
import java.util.Iterator;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * This class preprocess an event log by removing all traces whose events contain
 * missing information, i.e. missing timestamp, lifecycle, resource.
 * 
 * @author 	Martin Kaeppel
 * @version 12.10.2020
 */

public class Filtering {
	
	public Filtering() {
		
	}
	
	/**
	 * 	This methods checks the traces of the given event log whether the information passed to the method are available. Otherwise
	 *  the trace is removed from the event log.
	 */
	public XLog filterEventLog(XLog log, boolean timestamp, boolean activity, boolean originator, boolean lifecycle) {		
		XFactory factory = new XFactoryBufferedImpl();
		XLog filteredLog = factory.createLog();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			boolean check = true;
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				
				if(activity == true) {
					String extractedActivity = XConceptExtension.instance().extractName(currentEvent);
					if(extractedActivity == null) {
						check = false;
						break;
					}
				}
				
				if(originator == true) {
					String extractedOriginator = XOrganizationalExtension.instance().extractResource(currentEvent);
					if(extractedOriginator == null) {
						check = false;
						break;
					}
				}
				
				if(lifecycle == true) {
					String extractedLifecycle = XLifecycleExtension.instance().extractTransition(currentEvent);
					if(extractedLifecycle == null) {
						check = false;
						break;
					}
				}
				
				if(timestamp == true) {
					Date extractedTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
					if(extractedTimestamp == null) {
						check = false;
						break;
					}
				}
			}
			
			if(check == true) {
				filteredLog.add(currentTrace);
			}
			else {
				System.out.println("Remove: " + XConceptExtension.instance().extractName(currentTrace));
			}
		}
		
		return filteredLog;
	}
}
