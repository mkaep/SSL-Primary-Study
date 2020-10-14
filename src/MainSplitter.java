import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;

import loader.Loader;
import parser.Parser;
import splitter.SplitterByStartingTime;
import splitter.TestTrainObject;

public class MainSplitter {
	
	public static void main(String args[]) {
		List<String> eventLogs = new ArrayList<String>();
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\Helpdesk.xes");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2012\\BPI_Challenge_2012.xes.gz");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_1\\BPIC15_1.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_2\\BPIC15_2.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_3\\BPIC15_3.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_4\\BPIC15_4.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_5\\BPIC15_5.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2017\\BPI Challenge 2017.xes.gz");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2013\\BPI_Challenge_2013_closed_problems.xes.gz");
		
		SplitterByStartingTime splitter = new SplitterByStartingTime();
		Parser p = new Parser();
		for(String eventLog : eventLogs) {
			Loader loader = Loader.getInstance();
			XLog log = loader.getProcessLog(eventLog);
			TestTrainObject o = splitter.splitEventLog(log, 0.3);
			
			System.out.println(eventLog);
			System.out.println("Gesamt: "+p.getNumberOfTraces(log));
			System.out.println("Training: "+p.getNumberOfTraces(o.getTrainingLog()));
			System.out.println("Test: "+p.getNumberOfTraces(o.getTestLog()));

			System.out.println("--------------------------------------------------");

			
		}
	}

}
