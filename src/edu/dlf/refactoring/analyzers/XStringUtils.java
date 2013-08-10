package edu.dlf.refactoring.analyzers;

import org.apache.commons.lang3.StringUtils;

public class XStringUtils {
	
	private XStringUtils() throws Exception
	{
		throw new Exception();
	}

	public static String RemoveWhiteSpace(String text)
	{
		return text.replaceAll("\\s","");
	}

	public static int distance(String s1, String s2) {
		return StringUtils.getLevenshteinDistance(s1, s2);
	}
}
