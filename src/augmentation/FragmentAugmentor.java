package augmentation;

import java.util.concurrent.ThreadLocalRandom;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import util.IdGenerator;
/**
 * This class creates fragments of traces, that can be used for the augmentation
 * 
 * @author Martin Käppel
 *
 */
public class FragmentAugmentor extends Augmentor {
	
	public FragmentAugmentor() {
		
	}
	
	public XTrace subtrace(XTrace trace, int start, int end) {		
		if(start >= 0 && start <= end && end <= trace.size()) {
			XTrace subtrace = (XTrace) trace.clone();
			String oldCaseId = trace.getAttributes().get("concept:name").toString();
			XConceptExtension.instance().assignName(subtrace, oldCaseId+"_syn"+IdGenerator.getNewId());
			subtrace.clear();
			for(int i=0; i < trace.size(); i++) {
				if(i >= start && i <= end) {
					XEvent newEvent = (XEvent) trace.get(i).clone();
					subtrace.add(newEvent);
				}
			}
			return subtrace;
		}
		else {
			System.err.println("Out of Bounds Exception");
			return null;
		}
	}
	
	public XTrace createRandomFragment(XTrace trace) {
		int length = trace.size();
		int start = ThreadLocalRandom.current().nextInt(0, length);
		int end = ThreadLocalRandom.current().nextInt(0, length);
		
		if(start > end) {
			int temp = end;
			end = start;
			start = temp;
		}
		
		return subtrace(trace, start, end);
	}
	
	public XTrace createFragmentFromStart(XTrace trace) {
		int length = trace.size();
		int end = ThreadLocalRandom.current().nextInt(0, length);
		
		return subtrace(trace, 0, end);
	}
	
	public XTrace createFragmentFromStartKeepEnd(XTrace trace) {
		int length = trace.size();
		int end = ThreadLocalRandom.current().nextInt(0, length);
		XTrace tr = subtrace(trace, 0, end);
		tr.add(trace.get(trace.size()-1));
		return tr;
	}
}
