import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.deckfour.xes.model.XLog;

import loader.Loader;
import ml.Approach;
import ml.GenerativeLSTM;
import preprocessing.Filtering;
import reducer.ReducedLogContainer;
import reducer.Reducer;
import reducer.ReducerRandom;
import splitter.Splitter;
import splitter.SplitterByStartingTime;
import splitter.SplitterRandom;
import splitter.TestTrainObject;
import util.Serializer;

/**
 * Main class for the reduction of event logs
 * 
 * @author Martin Kaeppel
 */

public class MainNew {
	
	public static void main(String args[]) {
		//List with available event logs
		List<String> eventLogs = new ArrayList<String>();
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\Helpdesk.xes");
		/*
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2012\\BPI_Challenge_2012.xes.gz");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_1\\BPIC15_1.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_2\\BPIC15_2.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_3\\BPIC15_3.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_4\\BPIC15_4.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_5\\BPIC15_5.xes.xml");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2017\\BPI Challenge 2017.xes.gz");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2013\\BPI_Challenge_2013_closed_problems.xes.gz");
		*/
		
		//List with desired reductions
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
		
		//List with different splitter
		List<Splitter> splitters = new ArrayList<Splitter>();
		splitters.add(new SplitterRandom());
		splitters.add(new SplitterByStartingTime());
		
		//List with reducer
		List<Reducer> reducers = new ArrayList<Reducer>();
		reducers.add(new ReducerRandom());
		
		//List ratio test data trainings data
		List<Double> ratios = new ArrayList<Double>();
		ratios.add(0.3);
		ratios.add(0.2);
		
		//List of approaches that should be compared
		List<Approach> approaches = new ArrayList<Approach>();
		approaches.add(new GenerativeLSTM());
		
		boolean CONSIDER_LIFECYCLE = true;
		
		pipeline(eventLogs, desiredReductions, splitters, reducers, ratios, approaches, CONSIDER_LIFECYCLE);		
	}
	
	public static void pipeline(List<String> eventLogs, List<Double> desiredReductions, List<Splitter> splitters, List<Reducer> reducers, List<Double> ratios, List<Approach> approaches, boolean lifecycle) {
		for(String eventLog : eventLogs) {
			System.out.println(eventLog);
			
			//Create a directory for all created files
			String pathDataDirectory = FilenameUtils.getFullPath(eventLog)+"\\data";
			File dataDirectory = new File(pathDataDirectory);
			if(!dataDirectory.exists()) {
				dataDirectory.mkdir();
			}
			
			//Load event log
			Loader loader = Loader.getInstance();
			XLog log = loader.getProcessLog(eventLog);
			
			//Preprocessing
			Filtering filtering = new Filtering();
			XLog preprocessedLog = filtering.filterEventLog(log, true, true, true, lifecycle);
			
			//Storing the preprocessedLog
			Serializer serializer = new Serializer();
			serializer.serializeLog(preprocessedLog, pathDataDirectory, "log_preprocessed");


			//Splitting into test and training data using all available splitters
			for(Splitter splitter : splitters) {
				//Create an own directory for the splitter
				String pathSplitter = pathDataDirectory+"\\"+splitter.getClass().getSimpleName();
				
				File splitterDirectory = new File(pathSplitter);
				if(!splitterDirectory.exists()) {
					splitterDirectory.mkdir();
				}
				
				//Split preprocessed log into test and training data
				for(double sizeTestData : ratios) {
					String pathRatio = pathSplitter+"\\"+sizeTestData;
					
					File ratioDirectory = new File(pathRatio);
					if(!ratioDirectory.exists()) {
						ratioDirectory.mkdir();
					}
					
					TestTrainObject testTrainObject = splitter.splitEventLog(preprocessedLog, sizeTestData);
					XLog testLog = testTrainObject.getTestLog();
					XLog trainingLog = testTrainObject.getTrainingLog();
					
					//Persist test data
					serializer.serializeLog(testLog, pathRatio, "log_test");
					
					//Reduce the training log with regard to the given reduction factors
					for(Reducer reducer : reducers) {
						//Create within the splitter directory a directory for the reducer
						String pathReducer = pathRatio+"\\"+reducer.getClass().getSimpleName();
						
						File reducerDirectory = new File(pathReducer);
						if(!reducerDirectory.exists()) {
							reducerDirectory.mkdir();
						}
						
						Set<ReducedLogContainer> reducedLogs = new HashSet<ReducedLogContainer>();
						ReducedLogContainer trainingContainer = new ReducedLogContainer(trainingLog, "log_train_red_10");					
						ReducedLogContainer testContainer = new ReducedLogContainer(testLog, "log_test");

						reducedLogs.add(trainingContainer);
						reducedLogs.add(testContainer);
						
						for(Double reductionFactor : desiredReductions) {
							XLog reducedLog = reducer.reduce(trainingLog, reductionFactor.doubleValue());
							String dString = String.valueOf(reductionFactor);
							String[] splitted = dString.split("\\.");
							serializer.serializeLog(reducedLog, pathReducer, "log_train_red_"+splitted[0]+splitted[1]);
							ReducedLogContainer container = new ReducedLogContainer(reducedLog, "log_train_red_"+splitted[0]+splitted[1]);
							reducedLogs.add(container);
						}
						serializer.serializeLog(trainingLog, pathReducer, "log_train_red_10");

						
						//Create all necessary input files for the different approaches that should be compared
						for(Approach approach : approaches) {
							String pathApproach = pathReducer+"\\"+approach.getClass().getSimpleName();
							
							File approachDirectory = new File(pathApproach);
							if(!approachDirectory.exists()) {
								approachDirectory.mkdir();
							}
							approach.createInputFiles(preprocessedLog, reducedLogs, pathApproach, lifecycle);				
						}
						
					}	
				}
			}
		}	
	}
}


