package util;

public class IdGenerator {
	private static int newId = 0;

	private IdGenerator() {
		
	}
	
	public static int getNewId() {
		newId++;
		return newId;
	}
}
