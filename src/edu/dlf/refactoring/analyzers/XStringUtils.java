package edu.dlf.refactoring.analyzers;

public class XStringUtils {
	
	private XStringUtils() throws Exception
	{
		throw new Exception();
	}

	public static String RemoveWhiteSpace(String text)
	{
		return text.replaceAll("\\s","");
	}
}
