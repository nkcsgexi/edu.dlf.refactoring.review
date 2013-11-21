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
		if(array == null) return List.nil();
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
	
	public static <T, S> Equal<P2<T, S>> extendEqualToProduct(final Equal<T> eqt, 
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

	public static <T, S> F<P2<T, S>, T> getFirstElementInPFunc(T t, S s) {
		return new F<P2<T,S>, T>() {
			@Override
			public T f(P2<T, S> p) {
				return p._1();
		}};
	}
	
	public static <T, S> F<P2<T, S>, S> getSecondElementInPFunc(T t, S s) {
		return new F<P2<T,S>, S>() {
			@Override
			public S f(P2<T,S> p) {
				return p._2();
		}};
	}
	
	public static <T, S> F<T, P2<T, S>> appendElementFunc(final T t, final S s) {
		return new F<T, P2<T,S>>() {
			@Override
			public P2<T, S> f(T t1) {
				return P.p(t1, s);
		}};
	}
	
	public static <T, S> F<T, P2<S, T>> prependElementFunc(final S s, final T t) {
		return new F<T, P2<S, T>>() {
			@Override
			public P2<S, T> f(T t1) {
				return P.p(s, t1);
		}};
	}
	
	public static <T> F<List<T>, Integer> getListLengthFunc(T t) {
		return new F<List<T>, Integer>() {
			@Override
			public Integer f(List<T> list) {
				return list.length();
	}};}
	
	public static <T> F<T, List<T>> addSelfToMultipleMapper(final F<T, List<T>> mapper) {
		return new F<T, List<T>>() {
			@Override
			public List<T> f(T t) {
				return mapper.f(t).snoc(t);
		}};
	}
	
	public static <T> F2<List<T>, List<T>, List<T>> listAppender(T t) {
		return new F2<List<T>, List<T>, List<T>>() {
			@Override
			public List<T> f(List<T> list0, List<T> list1) {
				return list0.append(list1);
		}};
	}
	
	public static <T> List<T> createEmtpyList(T t) {
		return List.nil();
	}
}
