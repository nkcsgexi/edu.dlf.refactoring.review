package edu.dlf.refactoring.analyzers;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.Lists;

import fj.Equal;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;
import fj.data.Option;

public class FJUtils {

	private FJUtils() throws Exception
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
	
	public static <T> F<P2<T, T>, Boolean> convertEqualToProduct(final 
		Equal<T> eq) {
		return new F<P2<T,T>, Boolean>() {
			@Override
			public Boolean f(P2<T, T> pair) {
				return eq.eq(pair._1(), pair._2());
		}};
	}
	
	public static <T, S> F<P2<T, T>, P2<S, S>> extendMapper2Product(final F<T, S> 
		mapper) {
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
	
	public static <T> List<T> createEmptyList(T t) {
		return List.nil();
	}
	
	public static <T, S> F<T, S> getTypeConverter(T t, S s) {
		return new F<T, S>() {
			@Override
			public S f(T t) {
				return (S)t;
		}};
	}
	
	public static <T> List<T> getSubList(List<T> list, final int start, final 
		int end) {
		// where end - start is the length
		return list.zipIndex().filter(new F<P2<T,Integer>, Boolean>() {
			@Override
			public Boolean f(P2<T, Integer> pair) {
				int index = pair._2();
				return index >= start && index < end;
			}
		}).map(getFirstElementInPFunc((T)null, (Integer)null));
	}
	
	
	public static <T> F<T, Boolean> nonNullFilter(T t) {
		return new F<T, Boolean>(){
			@Override
			public Boolean f(T t) {
				return t != null;
		}};
	}
	
	public static <T> F<List<T>, T> getHeadFunc(T t) {
		return new F<List<T>, T>() {
			@Override
			public T f(List<T> list) {
				return list.head();
		}};
	}
	
	public static <T> F<T, Boolean> getIsNullFilter(T t) {
		return new F<T, Boolean>() {
			@Override
			public Boolean f(T t) {
				return t == null;
		}};
	}
	
	public static <T> List<P2<T, T>> getSamePairs(List<T> list1, List<T> list2, 
			Equal<T> eq) {
		List<P2<T, T>> allPairs = list1.bind(list2, pairFunction((T)null));
		return allPairs.filter(eq.eq().tuple());
	}
	
	public static <T> F<T, Boolean> andPredicates(final F<T, Boolean> p1, 
		final F<T, Boolean> p2) {
		return new F<T, Boolean>() {
			@Override
			public Boolean f(T t) {
				return p1.f(t) && p2.f(t);
		}};
	}
	
	public static <T> F<T, Boolean> orPredicates(final F<T, Boolean> p1, 
		final F<T, Boolean> p2) {
		return new F<T, Boolean>() {
			@Override
			public Boolean f(T t) {
				return p1.f(t) || p2.f(t);
		}};
	}
	
	public static <T> F<T, Boolean> negatePredicate(final F<T, Boolean> p) {
		return new F<T, Boolean>() {
			@Override
			public Boolean f(T t) {
				return !p.f(t);
		}};
	}
	
	public static <T> F<List<T>, Boolean> isEmpty(final T t) {
		return new F<List<T>, Boolean>() {
			@Override
			public Boolean f(List<T> list) {
				return list.isEmpty();
		}};
	}
	
	public static <T> java.util.List<T> toJavaList(final List<T> list) {
		return Lists.newArrayList(list.toCollection());
	}
}
