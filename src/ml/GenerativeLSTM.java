package ml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import parser.ActivityRoleTuple;
import parser.Parser;
import reducer.ReducedLogContainer;

/**
 * This class generates all necessary input file for machine learning
 * in the python program that can be found at https://github.com/AdaptiveBProcess/GenerativeLSTM.
 * 
 * 
 * @author Martin Käppel
 *
 */
public class GenerativeLSTM implements Approach {
	
	public GenerativeLSTM() {
		
	}
	
	public void createInputFiles(XLog originalLog, Set<ReducedLogContainer> reducedLogs, String path, boolean lifecycle) {
		Parser p = new Parser();
		Set<String> activities = null;
		if(lifecycle == true) {
			activities = p.getActivitiesWithLifecycle(originalLog);			
		}
		else {
			activities = p.getActivities(originalLog);
		}
		
		Map<String, Set<String>> roles = p.extractRoles(originalLog, lifecycle);
		
		// Create a map that returns the role to a given originator
		Map<String, String> originatorRoleMap = new HashMap<String, String>();
		for(String role : roles.keySet()) {
			Set<String> participants = roles.get(role);
			for(String s : participants) {
				originatorRoleMap.put(s, role);
			}
		}
		
		//Create indices for activities and roles
		Map<String, Integer> activityIndex = createActivityIndex(activities, "Start", "End");
		Map<String, Integer> roleIndex = createRoleIndex(roles.keySet(), "Start", "End");
		
		//Serialize files
		serializeActivityIndex(activityIndex, path);
		serializeRoleIndex(roleIndex, path);
		serializeActivityRoleTuple(p.getActivityRoleTuple(originalLog, lifecycle), path, activityIndex, roleIndex);
		
		
		//Transform reduced logs into csv format
		for(ReducedLogContainer log : reducedLogs) {
			Iterator<XTrace> logIterator = log.getLog().iterator();
			
			File pairFile = new File(path+"\\inp_"+log.getTitle()+".csv");
			if(pairFile.exists()) {
				pairFile.delete();			
			}
			pairFile = new File(path+"\\inp_"+log.getTitle()+".csv");

			try {
				FileWriter fileWriter = new FileWriter(pairFile, true);
				BufferedWriter writer = new BufferedWriter(fileWriter);
			    PrintWriter out = new PrintWriter(writer);
			    out.print("caseid,task,user,end_timestamp,role,duration,activity_index,role_index,dur_norm"+"\n");
				
				while(logIterator.hasNext()) {
					XTrace currentTrace = logIterator.next();
					String caseId = currentTrace.getAttributes().get(XConceptExtension.KEY_NAME).toString();
					Iterator<XEvent> traceIterator = currentTrace.iterator();
					while(traceIterator.hasNext()) {
						XEvent currentEvent = traceIterator.next();
						
						String line = "";
						
						String activity = currentEvent.getAttributes().get(XConceptExtension.KEY_NAME).toString();
						if(lifecycle == true) {
							String transition = currentEvent.getAttributes().get(XLifecycleExtension.KEY_TRANSITION).toString();
							activity = activity+"-"+transition;
						}
						//TO DO: Preprocessing if invalid data in event log
						String originator = currentEvent.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE).toString();
						String endTimestamp = XTimeExtension.instance().extractTimestamp(currentEvent).toString();
						String role = originatorRoleMap.get(originator);
						//Time is not supported until now
						String duration = "0";
						
						int aIndex = activityIndex.get(activity).intValue();
						int rIndex = roleIndex.get(role).intValue();
						
						String durNorm = "";

						line = line+caseId+","+activity+","+originator+","+endTimestamp+","+role+","+duration+","+aIndex+","+rIndex+","+durNorm+"\n";
						out.print(line);
					}	
				}   
				out.close();
			} 
			catch(IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
		
	private Map<String, Integer> createActivityIndex(Set<String> activities, String start, String end) {
		Map<String, Integer> acIndex = new HashMap<String, Integer>();
		
		acIndex.put(start, 0);
		
		int index = 1;
		for(String activity : activities) {
			acIndex.put(activity, index);
			index++;
		}
		acIndex.put(end, index);
		
		return acIndex;
	}
		
	private Map<String, Integer> createRoleIndex(Set<String> roles, String startRole, String endRole) {
		Map<String, Integer> roleIndex = new HashMap<String, Integer>();
		
		roleIndex.put(startRole, 0);
		
		int index = 1;
		for(String role : roles) {
			roleIndex.put(role, index);
			index++;
		}
		roleIndex.put(endRole, index);
		
		return roleIndex;
	}
	
	private void serializeActivityIndex(Map<String, Integer> activityIndex, String path) {
		StringBuilder sb = new StringBuilder();
		sb.append("Activity,Index");
		sb.append("\n");
		for(String s : activityIndex.keySet()) {
			sb.append(s);
			sb.append(",");
			sb.append(activityIndex.get(s));
			sb.append("\n");
		}
		
		BufferedWriter writer = null;
		File activityFile = new File(path+"\\inp_activities.csv");
		try {
			writer = new BufferedWriter(new FileWriter(activityFile));
			writer.write(sb.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void serializeRoleIndex(Map<String, Integer> roleIndex, String path) {
		StringBuilder sb = new StringBuilder();
		sb.append("Role,Index");
		sb.append("\n");
		for(String s : roleIndex.keySet()) {
			sb.append(s);
			sb.append(",");
			sb.append(roleIndex.get(s));
			sb.append("\n");
		}
		
		BufferedWriter writer = null;
		File roleFile = new File(path+"\\inp_roles.csv");
		try {
			writer = new BufferedWriter(new FileWriter(roleFile));
			writer.write(sb.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
		
	}
	
	
	private void serializeActivityRoleTuple(List<ActivityRoleTuple> pairs, String path, Map<String, Integer> activityIndex, Map<String, Integer> roleIndex) {
		File pairFile = new File(path+"\\inp_pairs.csv");
		if(pairFile.exists()) {
			pairFile.delete();			
		}
		pairFile = new File(path+"\\inp_pairs.csv");

		try {
			FileWriter fileWriter = new FileWriter(pairFile, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
		    PrintWriter out = new PrintWriter(writer);
		    out.print("Activity,Role\n");
			for(ActivityRoleTuple t : pairs) {
				out.print(activityIndex.get(t.getActivity())+","+roleIndex.get(t.getRole())+"\n");
			}
			out.close();
		} 
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
