package augmentation;

import java.util.Date;
import java.util.Iterator;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import util.IdGenerator;
import util.Util;

/**
 * This class modifies traces with augmentation techniques that are inspired by research in the
 * augmentation of time series.
 * 
 * @author Martin Käppel
 *
 */
public class TimeSeriesAugmentor extends Augmentor {
	public TimeSeriesAugmentor() {
		
	}
	
	/*
	 * @param shift: > 0 delays a trace in the future
	 * 				< 0 delays a trace back in time
	 */
	public XTrace shiftTrace(XTrace trace, long shift) {
		XTrace shiftedTrace = (XTrace) trace.clone();
		
		String oldCaseId = trace.getAttributes().get("concept:name").toString();
		XConceptExtension.instance().assignName(shiftedTrace, oldCaseId+"_syn"+IdGenerator.getNewId());
		
		Iterator<XEvent> it = shiftedTrace.iterator();
		
		while(it.hasNext()) {
			XEvent currentEvent = it.next();
			Date timestamp = Util.tryParse(currentEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
			XTimeExtension.instance().assignTimestamp(currentEvent, timestamp.getTime()+shift);
		}
		return shiftedTrace;
	}
	
	/* Stretches the duration of a trace
	 * @param stretchFactor
	 */
	public XTrace stretchTrace(XTrace trace, double stretchFactor) {
		XTrace stretchedTrace = (XTrace) trace.clone();
		
		String oldCaseId = trace.getAttributes().get("concept:name").toString();
		XConceptExtension.instance().assignName(stretchedTrace, oldCaseId+"_syn"+IdGenerator.getNewId());
		
		for(int i = 0; i < stretchedTrace.size()-1; i++) {
			Date firstTimestamp = Util.tryParse(trace.get(i).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
			Date secondTimestamp = Util.tryParse(trace.get(i+1).getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString());
			long distance = (long) ((secondTimestamp.getTime()-firstTimestamp.getTime())*stretchFactor);
			XTimeExtension.instance().assignTimestamp(stretchedTrace.get(i+1), Util.tryParse(stretchedTrace.get(i).getAttributes().get("time:timestamp").toString()).getTime()+distance);
		}
		return stretchedTrace;
	}
	

}
