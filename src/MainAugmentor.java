import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.deckfour.xes.model.XLog;

import augmentation.Augmentation;
import augmentation.Augmentor;
import augmentation.FragmentAugmentor;
import augmentation.NoiseAugmentor;
import augmentation.TimeSeriesAugmentor;
import loader.Loader;

/**
 * Main class that shows exemplarly the augmentation of event logs
 * 
 * 
 * @author Martin Kaeppel
 */
public class MainAugmentor {
	
	public static void main(String args[]) {
		//TO DO: What`s the best augmentation factor?
		int AUGMENTATION_FACTOR = 2;
		List<String> eventLogs = new ArrayList<String>();
		//The event logs listed the following list are some reduced variants of the original ones
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\data\\train_red_01.xes");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\data\\train_red_05.xes");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_1\\data\\train_red_01.xes");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_1\\data\\train_red_06.xes");
		eventLogs.add("I:\\Lab\\Real_Life_Event_Logs\\BPI_Challenge_2015_1\\data\\train_red_07.xes");

		Loader loader = Loader.getInstance();
		//Numbering the logs
		int i = 0;
		for(String eventLog : eventLogs) {
			XLog log = loader.getProcessLog(eventLog);
			String path = FilenameUtils.getFullPath(eventLog);

			TimeSeriesAugmentor timeSeriesAugmentor = new TimeSeriesAugmentor();
			FragmentAugmentor fragmentAugmentor = new FragmentAugmentor();
			NoiseAugmentor noiseAugmentor = new NoiseAugmentor();

			Map<Augmentor, Double> augmentors = new HashMap<Augmentor, Double>();
			//TO DO: Find the optimal weights for different augmentors
			augmentors.put(noiseAugmentor, new Double(0.5));
			augmentors.put(fragmentAugmentor, new Double(0.25));
			augmentors.put(timeSeriesAugmentor, new Double(0.25));
					
			Augmentation augmentation = new Augmentation();
			augmentation.augmentProcessLog(AUGMENTATION_FACTOR, augmentors, log, path, ""+i);
			i++;
		}
		
		}
}
