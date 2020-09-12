package parser.classifier;

public class ResourceClassifier extends Classifier {
	
	private int index;
	
	public ResourceClassifier(int index) {
		this.index = index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		return index+"";
	}
	
	@Override
	public boolean equals(Object o) {		
		if(!(o instanceof ResourceClassifier)) {
			return false;
		}
		else {
			ResourceClassifier e = (ResourceClassifier) o;
			return ((index == e.getIndex()));
		}		
	}
	
	public int hashCode() {
		return 0;
	}
	

}
