package parser;

/**
 * This class encapsulates the activity and the role of an event
 * @author Martin Kaeppel
 */
public class ActivityRoleTuple {
	
	private String activity;
	private String role;
	
	public ActivityRoleTuple(String activity, String role) {
		this.activity = activity;
		this.role = role;
	}
	
	public String getActivity() {
		return this.activity;
	}
	
	public String getRole() {
		return this.role;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ActivityRoleTuple)) {
			return false;
		}
		else {
			ActivityRoleTuple c = (ActivityRoleTuple) o;
			return (activity.equals(c.getActivity()) && role.equals(c.getRole()));
		}
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "("+activity+","+role+")";
	}
	

}
