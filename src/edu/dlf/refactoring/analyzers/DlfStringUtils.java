package edu.dlf.refactoring.analyzers;

import org.apache.commons.lang3.StringUtils;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.data.List;

public class DlfStringUtils {
	
	private DlfStringUtils() throws Exception
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
				double dis = DlfStringUtils.distance(s1, s2);
				double base = Math.max(s1.length(), s2.length());
				return 1.0 - dis/base;
	}}; 
	
	public static F2<String, String, Double> getCommonWordsPercentage = 
		new F2<String, String, Double>() {
			@Override
			public Double f(String name1, String name2) {
				final List<String> words1 = camelCaseSplitter.f(name1);
				final List<String> words2 = camelCaseSplitter.f(name2);
				int base = Math.min(words1.length(), words2.length());
				int common = words1.foldLeft(new F2<Integer, String, Integer>() {
					@Override
					public Integer f(Integer count, String word) {
						return words2.find(Equal.stringEqual.eq(word)).isSome() 
							? count + 1 : count;
				}}, 0);
				return (double)common/(double)base;
	}};
	
	
	public static F<String, List<String>> camelCaseSplitter = 
		new F<String, List<String>>() {
		@Override
		public List<String> f(String name) {
			String[] words = name.replaceAll("[^A-Za-z]", "").split
				(("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])"));
			return FJUtils.createListFromArray(words);
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
