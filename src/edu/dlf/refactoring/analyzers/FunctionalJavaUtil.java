package edu.dlf.refactoring.analyzers;

import java.util.Collection;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;
import fj.data.Option;

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
		return P.p(t, s);
	}
	
	public static <T> List<P2<T,T>> pairEqualElements(List<T> list1, 
		List<T> list2, final Equal<T> eqFun)
	{
		Buffer buffer = Buffer.empty();
		for(final T head = list1.head(); list1.isNotEmpty(); list1 = list1.drop(1))
		{
			Option<T> found = list2.find(new F<T, Boolean>() {
				@Override
				public Boolean f(T t) {
					return eqFun.eq(head, t);
				}
			});
			if(found.isSome())
			{
				buffer.snoc(P.p(head, found.some()));
			}
		}
		return buffer.toList();
	}

	public static <T> List<T> createListFromCollection(Collection<T> items) {
		if(items == null || items.isEmpty()) return List.nil();
		Buffer<T> buffer = Buffer.empty();
		for(T t : items)
		{
			buffer.snoc(t);
		}
		return buffer.toList();
	}
	
	
	public static <T> F2<T, T, P2<T, T>> pairFunction(T t)
	{
		return new F2<T, T, P2<T,T>>(){
			@Override
			public P2<T, T> f(T t1, T t2) {
				return P.p(t1, t2);
		}};
	}
	
	public static <T> F<P2<T, T>, Boolean> convertEqualToProduct(final Equal<T> eq) {
		return new F<P2<T,T>, Boolean>() {
			@Override
			public Boolean f(P2<T, T> pair) {
				return eq.eq(pair._1(), pair._2());
		}};
	}

	
}
