package augmentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import parser.Parser;
import util.IdGenerator;

/**
 * This class creates by extracted knowledge new traces
 * Warning: Very experimental, some bugs have to be fixed
 * 
 * @author Martin Käppel
 *
 */
public class GenericAugmentor extends Augmentor {
	XFactory factory = new XFactoryBufferedImpl();

	
	public GenericAugmentor() {
		
	}
	
	public XTrace generateTraceRandomlyStartEnd(int logSize, int length,
			Set<XAttribute> activities, 
			Set<XAttribute> startActivity,
			Set<XAttribute> endActivity
			) {
		
		
		XTrace generatedTrace = factory.createTrace();
		XConceptExtension.instance().assignName(generatedTrace, "syn_"+IdGenerator.getNewId());

		Set<XAttribute> normalActivities = new HashSet<XAttribute>();
		for(XAttribute a : activities) {
			if(!(startActivity.contains(a) || endActivity.contains(a))) {
				normalActivities.add(a);
				System.out.println(a.toString());

			}
		}
		
		
		List<XAttribute> listNormalActivities = new ArrayList<XAttribute>(normalActivities);
		
		//Add start event
		XEvent startEvent = factory.createEvent();
		List<XAttribute> listStartActivities = new ArrayList<XAttribute>(startActivity);
		Collections.shuffle(listStartActivities);
		String randomStartActivity = listStartActivities.get(0).toString();
		XConceptExtension.instance().assignName(startEvent, randomStartActivity);

		generatedTrace.add(startEvent);
		length--;
		while(length > 1) {
			XEvent event = factory.createEvent();
			Collections.shuffle(listNormalActivities);
			String randomActivity = listNormalActivities.get(0).toString();
			XConceptExtension.instance().assignName(event, randomActivity);
			generatedTrace.add(event);
			length--;
		}
		
		//Add end event
		XEvent endEvent = factory.createEvent();
		List<XAttribute> listEndActivities = new ArrayList<XAttribute>(endActivity);
		Collections.shuffle(listEndActivities);
		String randomEndActivity = listEndActivities.get(0).toString();
		XConceptExtension.instance().assignName(endEvent, randomEndActivity);
		generatedTrace.add(endEvent);
		
		
	Iterator<XEvent> it = generatedTrace.iterator();
		
		while(it.hasNext()) {
			XEvent k = it.next();
			System.out.print(k.getAttributes().get("concept:name")+"::::");
		}
		
		return generatedTrace;
	}
	
	
	
	
	public XTrace generateTraceRandomly(XLog log, int logSize, int length, long minimalDistance, long maximalDistance, Map<String, Integer> distributionActivities, Map<String, Integer> distributionOriginators,
	Set<XAttribute> activities, Map<String, Integer> originators
			
	) {
		Parser p = new Parser();
		XTrace generatedTrace = factory.createTrace();
		XConceptExtension.instance().assignName(generatedTrace, "syn_"+IdGenerator.getNewId());
		
		int[] actToGenerate = new int[p.getActivities(log).size()-2];
		for(int i = 0; i < p.getActivities(log).size()-2; i++) {
			actToGenerate[i] = i+1;
		}
		double[] propAct = new double[p.getActivities(log).size()-2];
		for(String s : distributionActivities.keySet()) {
			double g = (distributionActivities.get(s)*1.0)/logSize;
			propAct[p.getActivities(log).size()-1] = g;
		}
				
		//EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(actToGenerate, propAct);
		
		Date d = new Date();
		long previousTimestamp = d.getTime();
		while(length > 0) {
			XEvent event = factory.createEvent();
			//Select activity randomly
			Set<String> act = p.getActivities(log);
			List<String> actList = new ArrayList<String>(act);
			Collections.shuffle(actList);
			String randomActivity = actList.get(0);
			
			XConceptExtension.instance().assignName(event, randomActivity);
			
			//Assign originator randomly
			List<String> listResource = new ArrayList<String>(originators.keySet());
			
			System.out.println(originators);
			Collections.shuffle(listResource);
			String randomResource = listResource.get(0).toString();
			XOrganizationalExtension.instance().assignResource(event, randomResource);
			
			//Assign lifecylce to complete
			XLifecycleExtension.instance().assignStandardTransition(event, StandardModel.COMPLETE);
			
			//Random duration
			long timeStep = ThreadLocalRandom.current().nextLong(minimalDistance, maximalDistance);
			
			XTimeExtension.instance().assignTimestamp(event, previousTimestamp);
			previousTimestamp = previousTimestamp+timeStep;

			//Set classifier to the vent log
			XAttributeImpl t = new XAttributeLiteralImpl("Activity", event.getAttributes().get("concept:name").toString());
			event.getAttributes().put("Activity", t);

			XAttributeImpl r = new XAttributeLiteralImpl("Resource", event.getAttributes().get("org:resource").toString());
			event.getAttributes().put("Resource", r);
			
			generatedTrace.add(event);
			length--;
		}		
		return generatedTrace;
	}
}
