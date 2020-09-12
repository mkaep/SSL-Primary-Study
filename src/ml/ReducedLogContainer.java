package ml;

import org.deckfour.xes.model.XLog;
/**
 * This class encapsulates a reduced event log and the corresponding title that indicates the parameters of reduction
 * @author Martin Kaeppel
 */

public class ReducedLogContainer {
	private XLog log;
	private String title;
	
	public ReducedLogContainer(XLog log, String title) {
		this.log = log;
		this.title = title;
	}
	
	public XLog getLog() {
		return log;
	}
	
	public String getTitle() {
		return title;
	}

}
