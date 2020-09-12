import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;

import loader.Loader;
import parser.organizational.RoleExtractor;
import parser.Parser;

/**
 * Main class for the role extraction of event logs
 * 
 * @author Martin Kaeppel
 */
public class MainRoleExtraction {
	
	public static void main(String args[]) {
		RoleExtractor re = new RoleExtractor();
		Loader loader = Loader.getInstance();
		XLog log = loader.getProcessLog("I:\\Lab\\Real_Life_Event_Logs\\Helpdesk\\Helpdesk.xes");
		Map<String, Integer[]> map_without_lifecyle = re.extractProfiles(log, false);
		Map<String, Integer[]> map_with_lifecyle = re.extractProfiles(log, true);

		
		for(String s : map_without_lifecyle.keySet()) {
			String temp = s+"\t";
			Integer[] currentProfile = map_without_lifecyle.get(s);
			temp = temp+"[";
			for(int i=0; i < currentProfile.length; i++) {
				temp = temp+currentProfile[i]+",";
			}
			temp = temp+"]";
			
		}
		
		Parser p = new Parser();
		System.out.println(p.getOriginators(log).size());
		Map<String, Set<String>> roles = re.extractRoles(map_without_lifecyle, 0.85);
		for(String role : roles.keySet()) {
			System.out.println(role);
			System.out.println("\t"+roles.get(role));
		}
		
		
		for(String s : map_with_lifecyle.keySet()) {
			String temp = s+"\t";
			Integer[] currentProfile = map_with_lifecyle.get(s);
			temp = temp+"[";
			for(int i=0; i < currentProfile.length; i++) {
				temp = temp+currentProfile[i]+",";
			}
			temp = temp+"]";
			
		}
		
		System.out.println(p.getOriginators(log).size());
		Map<String, Set<String>> rolesWithLifecyle = re.extractRoles(map_with_lifecyle, 0.85);
		for(String role : rolesWithLifecyle.keySet()) {
			System.out.println(role);
			System.out.println("\t"+rolesWithLifecyle.get(role));
		}
		
		
	}

}
