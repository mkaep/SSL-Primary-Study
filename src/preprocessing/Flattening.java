package preprocessing;

import java.util.Iterator;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.classifier.ActivityClassifier;
import parser.Parser;
import parser.TraceVariant;
/**
 * Possible preprocessing step. Flats an event log, i.e. ensures that each trace variant occures only once
 * @author Martin Kaeppel
 */
public class Flattening {
	
	public Flattening() {
		
	}
	
	public XLog flatteningLog(XLog log) {
		Parser p = new Parser();
		Set<TraceVariant> variants = p.getTraceVariants(log);
		
		XFactory factory = new XFactoryBufferedImpl();
		XLog flattenedLog = factory.createLog();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			TraceVariant variant = new TraceVariant();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				ActivityClassifier classifier = new ActivityClassifier(currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString());
				variant.addEvent(classifier);
			}
			
			if(variants.contains(variant)) {
				flattenedLog.add(currentTrace);
				variants.remove(variant);
			}
		}
		
		return flattenedLog;
		
	}

}
