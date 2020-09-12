package reducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.Parser;

/**
 * This class provides different methods to reduce the event log
 * @author Martin Kaeppel
 */
public class Reducer {
	
	public Reducer() {
		
	}
	
	public XLog reduceRandom(XLog log, double targetSize) {
		Parser p = new Parser();
		List<String> caseIds = new ArrayList<String>(p.getCaseIds(log));
		int numberToRemove = (int)(p.getNumberOfTraces(log)*(1-targetSize));
		while(numberToRemove > 0) {
			Collections.shuffle(caseIds);
			caseIds.remove(0);
			numberToRemove--;
		}
		
		XFactory factory = new XFactoryBufferedImpl();
		XLog reducedLog = factory.createLog();
		
		Set<String> reducedIds = new HashSet<String>(caseIds);
		Iterator<XTrace> logIterator = log.iterator();
		while(logIterator.hasNext()) {
			XTrace currentTrace = logIterator.next();
			if(reducedIds.contains(currentTrace.getAttributes().get(XConceptExtension.KEY_NAME).toString())) {
				reducedLog.add(currentTrace);
			}
			
		}
		
		return reducedLog;
	}
	
	//TO DO: Extend to further variants; adapt to new version
	/*
	public XLog reduceUniformTypes(XLog log, double targetSize) {
		Parser p = new Parser();
		if(targetSize >= 0 && targetSize <= 1) {
			int sizeNumber = 0;
			sizeNumber = (int) Math.round(1/targetSize);
			XLog newProcessLog = null;
			
			
				List<Set<String>> sufficientLength = new ArrayList<Set<String>>();
				List<List<Classifier>> notSufficientLength = new ArrayList<List<Classifier>>();
				Set<TraceVariant> types = p.getTraceVariants(log);
				
				for(List<Classifier> element : types.keySet()) {
					if(types.get(element).getNumber() >= sizeNumber) {
						sufficientLength.add(types.get(element).getCaseIds());
					}
					else {
						notSufficientLength.add(element);
					}
					
				}
				
				//SOlange nicht leer fasse zu neuen Mengen zusammen
				while(notSufficientLength.size() != 0) {
					if(notSufficientLength.size() == 1) {
						//Kann nicht mehr gemerged werden
						sufficientLength.add(types.get(notSufficientLength.get(0)).getCaseIds());
						notSufficientLength.remove(0);
					}
					else {
						//Merge die zwei Vorderen
						Collections.shuffle(notSufficientLength);
						List<Classifier> firstElement = notSufficientLength.remove(0);
						Collections.shuffle(notSufficientLength);
						List<Classifier> secondElement = notSufficientLength.remove(0);
						//Merge first element and second Element
						//Wenn merge Menge groesser als benoetigte Lenge übernehme dieses Bucket anderenfalls, fuege es wieder hinzu....
						//Behalte Type 1 als Type bei....	
						Set<String> mergedSet = merge(types.get(firstElement).getCaseIds(), types.get(secondElement).getCaseIds());
						if(mergedSet.size() >= sizeNumber) {
							sufficientLength.add(mergedSet);
						}
						else {
							types.get(firstElement).setCaseIds(mergedSet);
							notSufficientLength.add(firstElement);
						}
					}	
				}
							
				//Selektiere von jedem Typ die notwendige Prozentzahl
				Set<String> selectCases = new HashSet<String>();
				
				
				for(Set<String> e : sufficientLength) {
					List<String> elementAsList = new ArrayList<String>();
					elementAsList.addAll(e);
					
					int reducedNumberOfTraces = 0;
					reducedNumberOfTraces = (int) Math.round(e.size()*percentage);
					
					while(reducedNumberOfTraces != 0) {
						Collections.shuffle(elementAsList);
						selectCases.add(elementAsList.remove(0));
						reducedNumberOfTraces--;	
					}
				}
				
				
				
				//Uebernehme nur die selected cases in einen neues Process Log
				newProcessLog = generateProcessLog(selectCases);
			
			
			return newProcessLog;
			
		}
		else {
			System.err.println("Reduktionsfaktor muss zwischen 0 und 1 liegen!");
			return null;
		}
		
	}
	*/

}
