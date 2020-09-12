package parser;

import java.util.ArrayList;
import java.util.List;

import parser.classifier.Classifier;
/**
 * This class defines the concept of a trace variant. Traces are considered as equal with regard to different criteria.
 * @author Martin Kaeppel
 */
public class TraceVariant {
	List<Classifier> events = new ArrayList<Classifier>();
	
	public TraceVariant() {
		
	}
	
	public void addEvent(Classifier classifier) {
		events.add(classifier);
	}
	
	public List<Classifier> getEvents() {
		return events;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof TraceVariant)) {
			return false;
		}
		else {
			TraceVariant c = (TraceVariant) o;
			return (events.equals(c.getEvents()));
		}
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < events.size(); i++) {
			sb.append(events.get(i));
			if(i != events.size()-1) {
				sb.append("\t-->");
			}
		}
		return sb.toString();
	}

}
