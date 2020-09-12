package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;

public class Util {
	public final static String ACTIVITIES = "activities";
	public final static String ORIGINATORS = "originators";
	public final static String CASE_ATTRIBUTES = "case_attributes";
	public final static String EVENT_ATTRIBUTES = "event_attributes";
	public final static String START_ACTIVITIES = "start";
	public final static String END_ACTIVITIES = "end";
	public final static String CASE_ATTRIBUTES_VALUES ="case_attributes_values";
	public final static String EXTRACT_VARIANTS = "extract_variants";
	public final static String CONTROL_FLOW ="control_flow_variants";
	public final static String RESOURCES = "resources_variants";
	public final static String CONTROL_FLOW_RESOURCES = "control_flow_resources_variants";
	public final static String[] SET_VALUES = new String[] {CASE_ATTRIBUTES, EVENT_ATTRIBUTES, START_ACTIVITIES, END_ACTIVITIES, CASE_ATTRIBUTES_VALUES, EXTRACT_VARIANTS, CONTROL_FLOW, RESOURCES, CONTROL_FLOW_RESOURCES};
	public final static Set<String> COMPLETE_PARSING = new HashSet<>(Arrays.asList(SET_VALUES));
	
	
	public final static List<String> formatStrings = Arrays.asList("yyyy-MM-dd'T'HH:mm:ssSSSXXX", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", "yyyy-MM-dd'T'HH:mm:ssXXX");

	//Different types of reduction
	public final static String RANDOM_REDUCTION = "rr";
	public final static String RANDOM_REDUCTION_KEEP_ACTIVITY = "rrka";
	public final static String UNIFORM_TYPE_REDUCTION = "utr";
	public final static String UNIFORM_TYPE_REDUCTION_KEEP_ACTIVITY = "utrka";
	
	
	public static Date tryParse(String dateString) {
		for(String formatString : formatStrings) {
			try {
				return new SimpleDateFormat(formatString).parse(dateString);
			}
			catch(ParseException e) {
				
			}
		}
		return null;
	}
	
	public static void printTrace(XTrace trace) {
		Iterator<XEvent> it = trace.iterator();
		System.out.print(trace.getAttributes().get("concept:name").toString()+": ");
		while(it.hasNext()) {
			System.out.print(it.next().getAttributes().get("concept:name")+" --->");
		}
		System.out.println();
	}
	
	public static void serialize(XLog log, String title, String path) {
		try {
			 XesXmlSerializer serializer = new XesXmlSerializer();
			 serializer.serialize(log, new FileOutputStream(new File(path+"/"+title+".xes")));						
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String fileName(String path) {
		File f = new File(path);
		return f.getName().split("\\.")[0];
	}

}
