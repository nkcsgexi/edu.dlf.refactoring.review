package edu.dlf.refactoring.analyzers;

public class StringUtils {
	
	private StringUtils() throws Exception
	{
		throw new Exception();
	}

	public static String RemoveWhiteSpace(String text)
	{
		return text.replaceAll("\\s","");
	}
}
