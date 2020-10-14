package ml;

import java.util.Set;

import org.deckfour.xes.model.XLog;

import reducer.ReducedLogContainer;

public interface Approach {
	
	public void createInputFiles(XLog referenceLog, Set<ReducedLogContainer> logs, String path, boolean lifecycle);

}
