package ml;

import org.deckfour.xes.model.XLog;
/**
 * This class encapsulates a training log and the corresponding test log
 * @author Martin Kaeppel
 */
public class TestTrainObject {
	private XLog trainingLog;
	private XLog testLog;
	
	public TestTrainObject(XLog trainingLog, XLog testLog) {
		this.trainingLog = trainingLog;
		this.testLog = testLog;
	}
	
	public XLog getTrainingLog() {
		return this.trainingLog;
	}
	
	public XLog getTestLog() {
		return this.testLog;
	}

}
