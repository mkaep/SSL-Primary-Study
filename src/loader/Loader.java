package loader;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FilenameUtils;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import com.google.common.io.Files;

import net.lingala.zip4j.ZipFile;


/**
 * This class extracts an XLog from a given xes, mxml, csv file or from archive formats zip and gz.
 * @author Martin Käppel
 * @version 1.0
 *
 */
public class Loader {
	private XesXmlParser parser;
	private static Loader instance;
	
	//Supported file extensions
	public final static String XES = "xes";
	public final static String CSV = "csv";
	public final static String MXML = "mxml";
	public final static String ZIP = "zip";
	public final static String GZ = "gz";
	public final static String XML = "xml";
	
	/**
	 * 
	 * @param path	Location of the log file or the archive that contains the log file
	 */
	private Loader() {
		parser = new XesXmlParser();
	}
	
	public static Loader getInstance() {
		if(Loader.instance == null) {
			Loader.instance = new Loader();
		}
		return Loader.instance;
	}
	
	/**
	 * Extracts a XLog from the file that contains the process log
	 * @return extracted XLog or if the file contains more than one or no process log null
	 */
	public XLog getProcessLog(String path) {
		File file = new File(path);
		
		//Extracts if necessary the log file from an archive
		File logFile = extractProcessLogFromArchive(file);
		
		if(logFile != null) {
			List<XLog> list = new ArrayList<XLog>();
			if(parser.canParse(logFile)) {
				try {
					list = parser.parse(logFile);
					if(list.size() > 1 || list.size() == 0) {
						System.err.println("The file contains no or more than one process log!");
						return null;
					}
					else {
						return list.get(0);
					}
				}
				catch(Exception e) {
					System.err.println(e);
				}
			}
			else {
				System.err.println("No valid log!");
			}
			return null;
			
		}
		else {
			System.err.println("File Type not supported");
			return null;
		}
	}
		
	/**
	 * Extracts if necessary the process log from an archive file
	 */
	private File extractProcessLogFromArchive(File file) {
		String fileExtension = FilenameUtils.getExtension(file.getName()); 
		if(XES.equals(fileExtension) || CSV.equals(fileExtension) || MXML.equals(fileExtension)) {
			return file;
		}
		else {
			if(ZIP.equals(fileExtension)) {
				return extractZip(file);
			}
			else {
				if(GZ.equals(fileExtension)) {
					return extractGz(file);
				}
				else {
					if(XML.equals(fileExtension)) {
						return extractXML(file);
					}
					else {
						return null;	
					}
				}
			}
		}		
	}
	
	private File extractXML(File file) {
		String[] splitted = file.getName().split("\\.");
		if(splitted.length == 3) {
			String extension = splitted[1];
			if(extension.equals(XES)) {
				File extractedFile = new File(file.getParent()+"\\"+splitted[0]+"."+extension);
				try {
					Files.copy(file, extractedFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return extractedFile;
			}
			else {
				System.err.println("No XES File in Archive!");
			}
		}
		else {
			System.err.println("Unknown Content in File");
		}
		return null;
	}
	/**
	 * Extracts a process log from a zip-archive
	 * 
	 * @return	the extracted process log or null if the archive contains more than one file
	 */
	private File extractZip(File file) {
		try {
			ZipFile archive = new ZipFile(file);
			if(archive.getFileHeaders().size() != 1) {
				System.err.print("The archive contains no or more than one file!");
				return null;
			}
			else {
				String name = archive.getFileHeaders().get(0).getFileName();
				archive.extractAll("data");
				File unzippedFile = new File("data\\"+name);
				return unzippedFile;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * Extracts a process log from a gz-archive
	 * 
	 * @return	the extracted process log or null if the archive contains more than one file
	 */
	private File extractGz(File file) {
		byte[] buffer = new byte[1024];
		try {
			String[] splitted = file.getName().split("\\.");
			if(splitted.length == 3) {
				String extension = splitted[1];
				if(extension.equals(XES)) {
					GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
					File unzippedFile = new File(file.getParent()+"\\"+splitted[0]+"."+extension);
					FileOutputStream out = new FileOutputStream(unzippedFile);
					
					int len;
			        while ((len = gzis.read(buffer)) > 0) {
			        	out.write(buffer, 0, len);
			        }
			 
			        gzis.close();
			    	out.close();
			    	return unzippedFile;
				}
				else {
					System.err.println("No XES File in Archive!");
					return null;
				}
			}
			else {
				System.err.println("Unknown Content in Archive!");
				return null;
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
