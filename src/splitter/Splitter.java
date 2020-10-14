package splitter;

import org.deckfour.xes.model.XLog;

public interface Splitter {
	
	public TestTrainObject splitEventLog(XLog log, double testSize);

}
