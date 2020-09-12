package parser.classifier;
/**
 * Defines the activity classifier. Two traces are considered as identical if the sequence of activities is the same.
 * @author Martin Kaeppel
 */
public class ActivityClassifier extends Classifier {
	private String name;
	
	public ActivityClassifier(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ActivityClassifier)) {
			return false;
		}
		else {
			ActivityClassifier c = (ActivityClassifier) o;
			return (name.equals(c.getName()));
		}
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
