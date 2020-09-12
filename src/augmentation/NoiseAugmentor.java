package augmentation;

import java.util.concurrent.ThreadLocalRandom;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import util.IdGenerator;

public class NoiseAugmentor extends Augmentor {
	
	public NoiseAugmentor() {
		
	}
	
	public XTrace replaceStartActivity(XTrace trace, XEvent start) {
		XTrace replacedTrace = (XTrace) trace.clone();
		replacedTrace.set(0, start);
		return replacedTrace;
	}
	
	
	public XTrace swapping(XTrace trace) {
		if(trace.size() >= 2) {
			int pos = ThreadLocalRandom.current().nextInt(1, trace.size()-1);
			XTrace swappedTrace = (XTrace) trace.clone();
			String oldCaseId = trace.getAttributes().get("concept:name").toString();
			XConceptExtension.instance().assignName(swappedTrace, oldCaseId+"_syn"+IdGenerator.getNewId());
			XEvent firstEvent = trace.get(pos);
			XEvent secondEvent = trace.get(pos+1);
			swappedTrace.set(pos, secondEvent);
			swappedTrace.set(pos+1, firstEvent);
			return swappedTrace;
		}
		else {
			return null;
		}
	}
		
	/*
	 * Verdopple ein zufaelliges Event, Probleme bei Zeit
	 */
	public XTrace doubleEvent(XTrace trace) {
		XTrace doubledEventTrace = (XTrace) trace.clone();
		String oldCaseId = trace.getAttributes().get("concept:name").toString();
		XConceptExtension.instance().assignName(doubledEventTrace, oldCaseId+"_syn"+IdGenerator.getNewId());
		int pos = ThreadLocalRandom.current().nextInt(0, trace.size());
		XEvent doubleEvent = trace.get(pos);
		doubledEventTrace.add(pos, doubleEvent);
		return doubledEventTrace;
	}
}
