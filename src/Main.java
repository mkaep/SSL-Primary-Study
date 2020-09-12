import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.deckfour.xes.model.XLog;

import loader.Loader;
import ml.PythonInput;
import ml.ReducedLogContainer;
import ml.Splitter;
import ml.TestTrainObject;
import reducer.Reducer;
import util.Serializer;

/**
 * Main class for the reduction of event logs
 * 
 * @author Martin Kaeppel
 */
public class Main {
	
	public static void main(String args[]) {
		List<String> eventLogs = new ArrayList<String>();
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\Helpdesk.xes");
		//eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2012\\BPI_Challenge_2012.xes.gz");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_1\\BPIC15_1.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_2\\BPIC15_2.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_3\\BPIC15_3.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_4\\BPIC15_4.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_5\\BPIC15_5.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2017\\BPI Challenge 2017.xes.gz");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2013\\BPI_Challenge_2013_closed_problems.xes.gz");
		
		List<Double> desiredReductions = new ArrayList<Double>();
		desiredReductions.add(0.1);
		desiredReductions.add(0.2);
		desiredReductions.add(0.3);
		desiredReductions.add(0.4);
		desiredReductions.add(0.5);
		desiredReductions.add(0.6);
		desiredReductions.add(0.7);
		desiredReductions.add(0.8);
		desiredReductions.add(0.9);
		
		pipeline(eventLogs, desiredReductions, 0.3, true);		
	}
	
	public static void pipeline(List<String> eventLogs, List<Double> desiredReductions, double sizeTestData, boolean lifecycle) {
		//Iterate over the paths of the given event logs
		for(String eventLog : eventLogs) {
			System.out.println(eventLog);
			
			String path = FilenameUtils.getFullPath(eventLog)+"\\data";
			
			//Create sub directories
			File directory = new File(path);
			if(!directory.exists()) {
				directory.mkdir();
			}
			
			//Load event log
			Loader loader = Loader.getInstance();
			XLog log = loader.getProcessLog(eventLog);

			//Split into test and training data
			Splitter splitter = new Splitter();
			TestTrainObject testTrainObject = splitter.splitEventLog(log, sizeTestData);
			XLog testLog = testTrainObject.getTestLog();
			XLog trainingLog = testTrainObject.getTrainingLog();

			//Persist training and test data
			Serializer serializer = new Serializer();
			serializer.serializeLog(trainingLog, path, "train_red_10");
			serializer.serializeLog(testLog, path, "test");
			
			//Reduce the trainings log with regard to the given reduction factors
			Reducer reducer = new Reducer();
			PythonInput pyInp = new PythonInput();
			
			Set<ReducedLogContainer> logs = new HashSet<ReducedLogContainer>();
			ReducedLogContainer trainingContainer = new ReducedLogContainer(trainingLog, "train_red_10");
			logs.add(trainingContainer);
			
			ReducedLogContainer testContainer = new ReducedLogContainer(testLog, "test");
			logs.add(testContainer);

			for(Double reductionFactor : desiredReductions) {
				XLog reducedLog = reducer.reduceRandom(trainingLog, reductionFactor.doubleValue());
				String dString = String.valueOf(reductionFactor);
				String[] splitted = dString.split("\\.");
				serializer.serializeLog(reducedLog, path, "train_red_"+splitted[0]+splitted[1]);
				ReducedLogContainer container = new ReducedLogContainer(reducedLog, "train_red_"+splitted[0]+splitted[1]);
				logs.add(container);
			}
			
			//Create all necessary files for machine learning (not augmented files)
			if(lifecycle == true) {
				pyInp.createInputFiles(log, logs, path, true);				
			}
			else {
				pyInp.createInputFiles(log, logs, path, false);				
			}	
		}	
	}
}
