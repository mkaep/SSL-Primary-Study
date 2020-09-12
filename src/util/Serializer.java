package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import parser.ActivityRoleTuple;
/**
 * Helper class for serializing the created files
 * @author Martin Kaeppel
 */
public class Serializer {
	
	public Serializer() {
		
	}
	
	public void serializeLog(XLog log, String path, String title) {
		try {
			XesXmlSerializer serializer = new XesXmlSerializer();
			serializer.serialize(log, new FileOutputStream(path+"/"+title+".xes"));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void serializeActivityIndex(Map<String, Integer> activityIndex, String path) {
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
		File activityFile = new File(path+"\\activities.csv");
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
	
	public void serializeRoleIndex(Map<String, Integer> roleIndex, String path) {
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
		File roleFile = new File(path+"\\roles.csv");
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
	
	
	public void serializeActivityRoleTuple(List<ActivityRoleTuple> pairs, String path, Map<String, Integer> activityIndex, Map<String, Integer> roleIndex) {
		File pairFile = new File(path+"\\pairs.csv");
		if(pairFile.exists()) {
			pairFile.delete();			
		}
		pairFile = new File(path+"\\pairs.csv");

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
