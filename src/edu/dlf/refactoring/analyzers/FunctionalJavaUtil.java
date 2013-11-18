package edu.dlf.refactoring.analyzers;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;

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
	
	public static <T, S> F<P2<T, T>, P2<S, S>> extendMapper2Product(final F<T, S> mapper) 
	{
		return new F<P2<T,T>, P2<S,S>>() {
			@Override
			public P2<S, S> f(P2<T, T> p) {
				return P.p(mapper.f(p._1()), mapper.f(p._2()));
		}};
	}
	
	public static <T, S> Equal<P2<T, S>> extendEqual2Product(final Equal<T> eqt, 
		final Equal<S> eqs) {
		return Equal.equal(new F<P2<T, S>, F<P2<T, S>,Boolean>>() {
			@Override
			public F<P2<T, S>, Boolean> f(final P2<T, S> p1) {
				return new F<P2<T,S>, Boolean>() {
					@Override
					public Boolean f(final P2<T, S> p2) {
						return eqt.eq(p1._1(), p2._1()) && eqs.eq(p1._2(), p2._2());
		}};}});
	}
	
	public static <T> Equal<T> getReferenceEq(T t)
	{
		return Equal.equal(new F<T, F<T,Boolean>>() {
			@Override
			public F<T, Boolean> f(final T t0) {
				return new F<T, Boolean>() {
					@Override
					public Boolean f(final T t1) {
						return t0 == t1;
		}};}});
	}


}
