package reducer;

import org.deckfour.xes.model.XLog;

public interface Reducer {
	
	public XLog reduce(XLog log, double targetSize);

}
