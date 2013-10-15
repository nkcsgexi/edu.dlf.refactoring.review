package edu.dlf.refactoring.analyzers;

import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class FunctionalJavaUtil {

	private FunctionalJavaUtil() throws Exception
	{
		throw new Exception();
	}
	
	public static <T> List<T> createListFromArray(T[] array)
	{
		Buffer<T> buffer = Buffer.empty();
		for(T t : array)
		{
			buffer.snoc(t);
		}
		return buffer.toList();
	}
	
	
	public static int getBufferLength(Buffer buffer)
	{
		return buffer.toList().length();
	}
	
	public static <T, S> P2<T, S> createPair(T t, S s)
	{
		return List.single(t).zip(List.single(s)).head();
	}
	
}
