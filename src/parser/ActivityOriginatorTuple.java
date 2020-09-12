package parser;

/**
 * This class encapsulates the activity and the originator of an event
 * @author Martin Kaeppel
 */
public class ActivityOriginatorTuple {
	
	private String activity;
	private String originator;
	
	public ActivityOriginatorTuple(String activity, String originator) {
		this.activity = activity;
		this.originator = originator;
	}
	
	public String getActivity() {
		return this.activity;
	}
	
	public String getOriginator() {
		return this.originator;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ActivityOriginatorTuple)) {
			return false;
		}
		else {
			ActivityOriginatorTuple c = (ActivityOriginatorTuple) o;
			return (activity.equals(c.getActivity()) && originator.equals(c.getOriginator()));
		}
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "("+activity+","+originator+")";
	}

}
