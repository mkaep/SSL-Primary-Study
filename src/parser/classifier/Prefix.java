package parser.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Prefix {
	List<String> prefix = new ArrayList<String>();
	
	public Prefix() {
		
	}
	
	public void append(String element) {
		prefix.add(element);
	}
	
	public List<String> getPrefix() {
		return prefix;
	}
	
	public void setPrefix(List<String> prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public String toString() {
		return prefix.toString();
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Prefix)) {
			return false;
		}
		else {
			Prefix c = (Prefix) o;
			return Arrays.equals(prefix.toArray(),c.getPrefix().toArray());
		}
		
	}

}
