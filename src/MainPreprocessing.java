import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;

import loader.Loader;
import parser.Parser;
import preprocessing.Filtering;

public class MainPreprocessing {
	
	public MainPreprocessing() {
		
	}
	
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
		
		Parser p = new Parser();
		for(String eventLog : eventLogs) {
			Loader loader = Loader.getInstance();
			XLog log = loader.getProcessLog(eventLog);
			System.out.println(eventLog);
			Filtering filtering = new Filtering();
			XLog filteredLog = filtering.filterEventLog(log, true, true, true, true);
			System.out.println("Davor: " + p.getNumberOfTraces(log));
			System.out.println("Danach: "+ p.getNumberOfTraces(filteredLog));
			System.out.println("------------------------------------------------");
		}
	}

}
