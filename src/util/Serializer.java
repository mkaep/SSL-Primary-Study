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
}
