package parser.organizational;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import parser.Parser;
/**
 * This class extracts roles in an event log
 * 
 * @author Martin Kaeppel
 */
public class RoleExtractor {
	
	public RoleExtractor() {
		
	}
	
	public Map<String, Integer[]> extractProfiles(XLog log, boolean lifecycle) {
		Parser p = new Parser();
		
		Set<String> originators = p.getOriginators(log);
		Set<String> activities = new HashSet<String>();
		if(lifecycle == false) {
			activities = p.getActivities(log);
		}
		else {
			activities = p.getActivitiesWithLifecycle(log);
		}
		
		Map<String, Integer[]> profiles = new HashMap<String, Integer[]>();
		Map<String, Integer> activityIndex = new HashMap<String, Integer>();
		
		int counter = 0;
		for(String activity : activities) {
			activityIndex.put(activity, counter);
			counter++;
		}
		
		int numberOfActivities = activities.size();
		for(String originator : originators) {
			Integer[] profile = new Integer[numberOfActivities];
			for(int i = 0; i < profile.length; i++) {
				profile[i] = 0;
			}
			profiles.put(originator, profile);
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String originator = XOrganizationalExtension.instance().extractResource(currentEvent);
				if(originator != null) {
					int actInd;
					if(lifecycle == false) {
						actInd = activityIndex.get(XConceptExtension.instance().extractName(currentEvent)).intValue();
					}
					else {
						actInd = activityIndex.get(XConceptExtension.instance().extractName(currentEvent)+"-"+XLifecycleExtension.instance().extractTransition(currentEvent)).intValue();
					}
					Integer[] currentProfile = profiles.get(originator);
					currentProfile[actInd] = currentProfile[actInd]+1;
				}
			}
		}
		
		return profiles;
	}
	
	public Map<String, Set<String>> extractRoles(Map<String, Integer[]> profiles, double simThreshold) {
		Map<String, Set<String>> roles = new HashMap<String, Set<String>>();
		
		Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);
		
		for(String originator : profiles.keySet()) {
			g.addVertex(originator);
		}
		
		Set<String> originators = profiles.keySet();
		for(String s1 : originators) {
			for(String s2 : originators) {
				double correlation = calculatePearsonCorrelation(profiles.get(s1), profiles.get(s2));
				if(!g.containsEdge(s1, s2)) {
					if(!g.containsEdge(s2, s1)) {
						if((correlation > simThreshold) && !(s1.equals(s2))) {
							g.addEdge(s1, s2);
						}
					}
				}
			}
		}
		
		ConnectivityInspector<String, DefaultEdge> subgraphAlgorithm = new ConnectivityInspector<String, DefaultEdge>(g);
		List<Set<String>> subgraphs = subgraphAlgorithm.connectedSets();
		
		int i=1;
		for(Set<String> s : subgraphs) {
			roles.put("Rolle "+i, s);
			i++;
		}
		
		return roles;
	}
		
	private double calculatePearsonCorrelation(Integer[] x, Integer[] y) {
		double correlation = 0.0;
		double x_mean = 0;
		double y_mean = 0;
		
		for(int i = 0; i < x.length; i++) {
			x_mean = x_mean+x[i];
		}
		x_mean = x_mean/(1.0*x.length);
		
		for(int j = 0; j < y.length; j++) {
			y_mean = y_mean+y[j];
		}
		y_mean = y_mean/(1.0*y.length);
		
		//Calculate Covariance
		double cov = 0;
		for(int i = 0; i < x.length; i++) {
			cov = cov+(x[i]-x_mean)*(y[i]-y_mean);
		}
		
		double var_x = 0;
		for(int i = 0; i < x.length; i++) {
			var_x = var_x + (x[i]-x_mean)*(x[i]-x_mean);
		}
		double var_y = 0;
		for(int i = 0; i < y.length; i++) {
			var_y = var_y + (y[i]-y_mean)*(y[i]-y_mean);
		}
		
		correlation = cov/(Math.sqrt(var_x*var_y));
		return correlation;
	}
	

}
