import org.deckfour.xes.model.XLog;

import loader.Loader;
import parser.Parser;
import splitter.Splitter;
import splitter.SplitterRandom;
import splitter.TestTrainObject;

/**
 * Main class for splitting an event log into training and testdata
 * 
 * @author Martin Kaeppel
 */
public class MainSplit {
	
	public static void main(String args[]) {
		Loader loader = Loader.getInstance();
		XLog log = loader.getProcessLog("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\Helpdesk.xes");
		

		Splitter splitter = new SplitterRandom();
		TestTrainObject o = splitter.splitEventLog(log, 0.3);
		
		Parser p = new Parser();
		System.out.println("Traces Ursprungslog: "+p.getNumberOfTraces(log));
		System.out.println("Traces Test Log: "+p.getNumberOfTraces(o.getTestLog()));
		System.out.println("Traces Training Log: "+p.getNumberOfTraces(o.getTrainingLog()));
		
		
	}

}
