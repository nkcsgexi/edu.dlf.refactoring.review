package edu.dlf.refactoring.analyzers;

import org.apache.commons.lang3.StringUtils;

import fj.F;
import fj.F2;

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
	
	public static F2<String, String, Double> getSamePartPercentage = 
		new F2<String, String, Double>() {
			@Override
			public Double f(String s1, String s2) {
				double dis = XStringUtils.distance(s1, s2);
				double base = Math.max(s1.length(), s2.length());
				return 1.0 - dis/base;
	}}; 
	
	public static F<StringBuilder, F<String, StringBuilder>> stringCombiner = 
		new F<StringBuilder, F<String, StringBuilder>>(){
		@Override
		public F<String, StringBuilder> f(final StringBuilder builder) {
			return new F<String, StringBuilder>() {
				@Override
				public StringBuilder f(String s) {
					return builder.append(s);
				}};}};
}
