package parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.classifier.ActivityClassifier;
import parser.classifier.Prefix;
import parser.organizational.RoleExtractor;

/**
 * 
 * @author Martin Käppel
 * This class provides all necessary parsing methods to extract all necessary information from an event log
 */
public class Parser {
	
	public Parser() {
		
	}
	
	/**
	 * Returns a set with all case ids
	 * 
	 * @param log
	 * @return
	 */
	public Set<String> getCaseIds(XLog log) {
		Set<String> caseIds = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			caseIds.add(currentTrace.getAttributes().get(XConceptExtension.KEY_NAME).toString());
		}
		return caseIds;
	}
	
	/**
	 * Returns the number of traces in a given log
	 * @param log
	 * @return
	 */
	public int getNumberOfTraces(XLog log) {
		int number = 0;
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			number++;
			logIterator.next();
		}
		
		return number;
	}
	
	/**
	 * Returns all used extensions in a given log
	 * @param log
	 * @return
	 */
	public Set<XExtension> getExtensions(XLog log) {
		return log.getExtensions();
	}
	
	/**
	 * Returns all used lifecycle transition in a given log
	 * 
	 * @param log
	 * @return
	 */
	public Set<String> getLifecyleTransitions(XLog log) {
		Set<String> transitions = new HashSet<String>();
		
		if(checkNecessaryExtension(log, XLifecycleExtension.instance())) {
			Iterator<XTrace> logIterator = log.iterator();
			while(logIterator.hasNext()) {
				XTrace currentTrace = logIterator.next();
				Iterator<XEvent> traceIterator = currentTrace.iterator();
				
				while(traceIterator.hasNext()) {
					XEvent currentEvent = traceIterator.next();
					if(currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION) != null) {
						transitions.add(currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString());
					}
				}
			}
			
		}
		else {
			System.err.println("Log does not support lifecyle extension!");
		}
		
		return transitions;
	}
	
	private boolean checkNecessaryExtension(XLog log, XExtension extension) {
		if(getExtensions(log).contains(extension)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Returns all roles with all participants of the roles. 
	 * 
	 * @param log			Given event log
	 * @param lifecyle		If true the lifecycle is considered otherwise the lifecycle is ignored
	 * @return
	 */
	public Map<String, Set<String>> extractRoles(XLog log, boolean lifecyle) {
		RoleExtractor roleExtractor = new RoleExtractor();
		
		Map<String, Integer[]> profiles = null;
		if(lifecyle == false) {
			profiles = 	roleExtractor.extractProfiles(log, false);
		}
		else {
			profiles = 	roleExtractor.extractProfiles(log, true);
		}
		Map<String, Set<String>> roles = roleExtractor.extractRoles(profiles, 0.85);
		
		return roles;
	}
	
	/**
	 * Extracts roles that are defined in the event log
	 * 
	 * @param log
	 * @return
	 */
	public Set<String> getRolesFromEventLog(XLog log) {
		Set<String> roles = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String role = XOrganizationalExtension.instance().extractRole(currentEvent);
				if(role != null) {
					roles.add(role);
				}
			}
		}
		return roles;
	}
	
	/**
	 * Extracts groups that are defined in the event log
	 * @param log
	 * @return set of groups
	 */
	public Set<String> getGroups(XLog log) {
		Set<String> groups = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				if(currentEvent.getAttributes().get(XOrganizationalExtension.KEY_GROUP) != null) {
					groups.add(currentEvent.getAttributes().get(XOrganizationalExtension.KEY_GROUP).toString());
				}
			}
		}
		return groups;
		
	}
	
	/**
	 * Returns the minimal duration of a trace in a given event log
	 * @param log
	 * @return minimal duration of a trace
	 */
	public long getMinimalDuration(XLog log) {
		long minimalDuration = Long.MAX_VALUE;
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			boolean first = true;
			Date firstTimestamp = null;
			Date lastTimestamp = null;
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				if(first == true) {
					firstTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
					//Fuer den Fall das Trace nur aus einem Event besteht.
					if(currentTrace.size() == 1) {
						lastTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
					}
					first = false;
					continue;
				}
				else {
					lastTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
				}				
			}
			
			long duration = lastTimestamp.getTime()-firstTimestamp.getTime();
			if(duration < minimalDuration) {
				minimalDuration = duration;
			}
		}
		
		return minimalDuration;
	
	}
	
	/**
	 * Returns the maximal duration of a trace in a given event log
	 * @param log
	 * @return maximal duration of a trace
	 */
	public long getMaximalDuration(XLog log) {
		long maximalDuration = Long.MIN_VALUE;
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			boolean first = true;
			Date firstTimestamp = null;
			Date lastTimestamp = null;
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				if(first == true) {
					firstTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
					//Fuer den Fall das Trace nur aus einem Event besteht.
					if(currentTrace.size() == 1) {
						lastTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
					}					
					first = false;
					continue;
				}
				else {
					lastTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent);
				}
			}
			long duration = lastTimestamp.getTime()-firstTimestamp.getTime();
			if(duration > maximalDuration) {
				maximalDuration = duration;
			}
		}
		return maximalDuration;
	}
	
	/**
	 * Returns the number of events of a event log
	 * @param log
	 * @return number of events in the event log
	 */
	public int getNumberOfEvents(XLog log) {
		int numberOfEvents = 0;
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				traceIterator.next();
				numberOfEvents++;
			}
		}
		return numberOfEvents;
	}
	
	/**
	 * Calculates the average of events per trace
	 * @param log
	 * @return events per trace
	 */
	public double getEventsPerTrace(XLog log) {
		return getNumberOfEvents(log)/(getNumberOfTraces(log)*1.0);
	}
	
	/**
	 * Returns the length of the shortest trace in a given event log
	 * @param log
	 * @return length of the shortest trace
	 */
	public int getMinimalEventsPerTrace(XLog log) {
		int minimum = Integer.MAX_VALUE;
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			int lengthOfTrace = 0;
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				traceIterator.next();
				lengthOfTrace++;
			}
			
			if(lengthOfTrace < minimum) {
				minimum = lengthOfTrace;
			}
		}
		return minimum;
	}
	
	/**
	 * Returns the maximal length of a trace in a given event log
	 * @param log
	 * @return length of the longest trace
	 */
	public int getMaximalEventsPerTrace(XLog log) {
		int maximum = Integer.MIN_VALUE;
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			int lengthOfTrace = 0;
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				traceIterator.next();
				lengthOfTrace++;
			}
			
			if(lengthOfTrace > maximum) {
				maximum = lengthOfTrace;
			}
		}
		return maximum;
	}
	
	/**
	 * Returns a set of all activities occuring in a given event log
	 * @param log
	 * @return set of occuring activities
	 */
	public Set<String> getActivities(XLog log) {
		Set<String> activities = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				activities.add(currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString());
			}
		}
		
		return activities;
	}
	
	/** 
	 * Returns a set of all activities concatenated with lifecycle transition in a given event log. If a transition is missing, the transition is handled as a empty string
	 * @param log
	 * @return set of activities (with lifecycle)
	 */
	public Set<String> getActivitiesWithLifecycle(XLog log) {
		Set<String> activities = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
				
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String lifecycle = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
				String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
				if(lifecycle != null) {
					activities.add(activity+"-"+lifecycle);
				}
				else {
					activities.add(activity);
				}
			}
		}
		return activities;
	}
	
	/**
	 * Returns a set of all originators in a given event log
	 * @param log
	 * @return all executing resources in the event log
	 */
	public Set<String> getOriginators(XLog log) {
		Set<String> originators = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				if(currentEvent.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE) != null) {
					originators.add(currentEvent.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE).toString());
				}
			}
		}
		
		return originators;
	}
	
	/**
	 * Returns a set of TraceVariants focussing on the sequence of activities
	 * @param log
	 * @return Set of the variant
	 */
	public Set<TraceVariant> getTraceVariants(XLog log) {
		Set<TraceVariant> variants = new HashSet<TraceVariant>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			TraceVariant variant = new TraceVariant();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				ActivityClassifier classifier = new ActivityClassifier(currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString());
				variant.addEvent(classifier);
			}
			variants.add(variant);
		}
		
		return variants;
	}
	
	/**
	 * Returns a set of TraceVariants by considering the lifecycle of the activities
	 * @param log
	 */
	public Set<TraceVariant> getTraceVariantsWithLifecycle(XLog log) {
		Set<TraceVariant> variants = new HashSet<TraceVariant>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			TraceVariant variant = new TraceVariant();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				
				String lifecycle = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
				String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
				
				ActivityClassifier classifier = null;
				if(lifecycle != null) {
					classifier = new ActivityClassifier(activity+"-"+lifecycle);
				}
				else {
					classifier = new ActivityClassifier(activity);
				}
				variant.addEvent(classifier);
			}
			variants.add(variant);
		}
		
		return variants;
	}
	
	/**
	 * Count the number of occurence of each TraceVariant
	 * @param log
	 * @param lifecycle
	 */
	public Map<TraceVariant, Integer> getCountedTraceVariants(XLog log) {
		Map<TraceVariant, Integer> countedVariants = new HashMap<TraceVariant, Integer>();
		
		Set<TraceVariant> variants = getTraceVariants(log);
		
		for(TraceVariant variant : variants) {
			countedVariants.put(variant, new Integer(0));
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			TraceVariant variant = new TraceVariant();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				ActivityClassifier classifier = new ActivityClassifier(currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString());
				variant.addEvent(classifier);
			}
			
			countedVariants.put(variant, countedVariants.get(variant)+1);
		}
		
		return countedVariants;
	}
	
	/**
	 * Count the number of occurence of each TraceVariant by considering the lifecycle
	 * @param log
	 * @param lifecycle
	 */
	public Map<TraceVariant, Integer> getCountedTraceVariantsWithLifecycle(XLog log) {
		Map<TraceVariant, Integer> countedVariants = new HashMap<TraceVariant, Integer>();
		
		Set<TraceVariant> variants = getTraceVariantsWithLifecycle(log);
		
		for(TraceVariant variant : variants) {
			countedVariants.put(variant, new Integer(0));
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			TraceVariant variant = new TraceVariant();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String lifecycle = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
				String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
				
				ActivityClassifier classifier = null;
				if(lifecycle != null) {
					classifier = new ActivityClassifier(activity+"-"+lifecycle);
				}
				else {
					classifier = new ActivityClassifier(activity);
				}				variant.addEvent(classifier);
			}
			
			countedVariants.put(variant, countedVariants.get(variant)+1);
		}
		
		return countedVariants;
	}
	
	/**
	 * Extracts all prefixes of a given event log
	 * @param log
	 * @param lifecycle
	 */
	public Set<Prefix> getPrefixes(XLog log, boolean lifecycle) {
		Set<Prefix> prefixes = new HashSet<Prefix>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			Prefix p = new Prefix();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				
				if(lifecycle == true) {
					String transition = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
					if(transition == null) {
						transition="";
					}
					String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
					p.append(activity+"-"+transition);
				}
				else {
					String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
					p.append(activity);		
				}
				
				Prefix nextPrefix = new Prefix();
				nextPrefix.setPrefix(new ArrayList<String>(p.getPrefix()));
				
				prefixes.add(nextPrefix);
				
			}
		}
		
		return prefixes;
	}
	
	/**
	 * Extracts all start activities
	 * @param log
	 */
	public Set<String> getStartActivities(XLog log) {
		Set<String> startActivities = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				startActivities.add(currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString());
				break;
			}
			
		}
		return startActivities;
	}
	
	/**
	 * Extracts all start activities considering the lifecycle
	 * @param log
	 */
	public Set<String> getStartActivitiesWithLifecycle(XLog log) {
		Set<String> startActivities = new HashSet<String>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
				String lifecycle = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
				startActivities.add(activity+"-"+lifecycle);
				break;
			}
			
		}
		return startActivities;
		
	}
	
	/**
	 * Calculates the distribution of TraceVariants TO DO: Lifecycle und weitere Varianten Arten
	 * @param variants
	 * @param log
	 */
	public Map<TraceVariant, DistributionObject> getDistribution(Set<TraceVariant> variants, XLog log) {
		Map<TraceVariant, DistributionObject> distribution = new HashMap<TraceVariant, DistributionObject>();
		
		for(TraceVariant e : variants) {
			distribution.put(e, new DistributionObject(0.0, 0));
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			
			TraceVariant tempVariant = new TraceVariant();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				ActivityClassifier classifier = new ActivityClassifier(currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString());
				tempVariant.addEvent(classifier);
			}
			
			if(distribution.keySet().contains(tempVariant)) {
				DistributionObject oldValue = distribution.get(tempVariant);
				DistributionObject newValue = new DistributionObject(oldValue.getRelativeFrequency(), oldValue.getNumber()+1);
				distribution.put(tempVariant, newValue);
			}
			else {
				System.err.println("Fehler: Element gibt es in dieser Distribution nicht");
				return null;
			}
		}
		
		int size = getNumberOfTraces(log);
		for(TraceVariant e : distribution.keySet()) {
			DistributionObject oldValue = distribution.get(e);
			DistributionObject newValue = new DistributionObject(oldValue.getNumber()/(size*1.0), oldValue.getNumber());
			distribution.put(e, newValue);
		}
		
		return distribution;
	}
	
	/**
	 * Extracts all tuples of activities and corresponding resource (with considering the lifecycle of an event)
	 */
	public List<ActivityOriginatorTuple> getActivityOriginatorTuple(XLog log, boolean lifecycle) {
		List<ActivityOriginatorTuple> pairs = new ArrayList<ActivityOriginatorTuple>();
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String activity = XConceptExtension.instance().extractName(currentEvent);
				String transition = null;
				if(lifecycle == true) {
					transition = XLifecycleExtension.instance().extractTransition(currentEvent);
					activity = activity+"-"+transition;
				}
				String originator = XOrganizationalExtension.instance().extractResource(currentEvent);
				
				if(activity != null && originator != null) {
					ActivityOriginatorTuple tuple = new ActivityOriginatorTuple(activity, originator);
					pairs.add(tuple);
				}
			}
		}
		
		return pairs;
	}
	
	/**
	 * Extracts all tuples of activities and corresponding role (roles are extracted) (with considering the lifecycle of an event)
	 */
	public List<ActivityRoleTuple> getActivityRoleTuple(XLog log, boolean lifecycle) {
		List<ActivityRoleTuple> pairs = new ArrayList<ActivityRoleTuple>();
		
		
		Map<String, Set<String>> roleOriginatorMap = null;
		if(lifecycle == false) {
			roleOriginatorMap = extractRoles(log, false);
		}
		else {
			roleOriginatorMap = extractRoles(log, true);
		}
		Map<String, String> originatorRoleMap = new HashMap<String, String>();
		
		for(String s : roleOriginatorMap.keySet()) {
			Set<String> participantsOfRole = roleOriginatorMap.get(s);
			for(String originator : participantsOfRole) {
				originatorRoleMap.put(originator, s);
			}
		}
		
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			
			Iterator<XEvent> traceIterator = currentTrace.iterator();
			while(traceIterator.hasNext()) {
				XEvent currentEvent = traceIterator.next();
				String activity = XConceptExtension.instance().extractName(currentEvent);
				String transition = null;
				if(lifecycle == true) {
					transition = XLifecycleExtension.instance().extractTransition(currentEvent);
					activity = activity+"-"+transition;
				}
				String originator = XOrganizationalExtension.instance().extractResource(currentEvent);
				
				if(activity != null && originator != null) {
					ActivityRoleTuple tuple = null;
					tuple = new ActivityRoleTuple(activity,originatorRoleMap.get(originator));
					pairs.add(tuple);
				}
			}
		}
		
		return pairs;
		
	}
}
