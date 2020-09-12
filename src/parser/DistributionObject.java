package parser;
/**
 * This class indicates the relative and absolute frequency of a type (event, trace, etc.)
 * @author Martin Kaeppel
 */
public class DistributionObject {
	
	private double relativeFrequency;
	private int number;
	
	public DistributionObject(double relativeFrequency, int number) {
		this.relativeFrequency = relativeFrequency;
		this.number = number;
	}
	
	public double getRelativeFrequency() {
		return relativeFrequency;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setRelativeFrequency(double relativeFrequency) {
		this.relativeFrequency = relativeFrequency;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return "("+number+","+relativeFrequency+")";
	}

}
