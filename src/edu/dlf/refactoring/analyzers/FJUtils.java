package edu.dlf.refactoring.analyzers;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.collect.Lists;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;
import fj.data.Option;

public class FJUtils {

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
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
	
	
	public static <T> F2<T, T, P2<T, T>> pairFunction(T t) {
		return new F2<T, T, P2<T,T>>(){
			@Override
			public P2<T, T> f(T t1, T t2) {
				return P.p(t1, t2);
		}};
	}
	
	public static <T, S> F2<T, S, P2<T, S>> pairFunction(T t, S s) {
		return new F2<T, S, P2<T,S>>(){
			@Override
			public P2<T, S> f(T t, S s) {
				return P.p(t, s);
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
		list1 = list1.nub(eq);
		list2 = list2.nub(eq);
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
	
	public static <T> F<List<T>, Boolean> isListEmpty(final T t) {
		return new F<List<T>, Boolean>() {
			@Override
			public Boolean f(List<T> list) {
				return list.isEmpty();
		}};
	}
	
	public static <T> java.util.List<T> toJavaList(final List<T> list) {
		return Lists.newArrayList(list.toCollection());
	}
	
	public static <T, S, K> F2<T, S, K> deTuple(final F<P2<T, S>, K> func) {
		return new F2<T, S, K>() {
			@Override
			public K f(T t, S s) {
				return func.f(P.p(t, s));
			}
		};
	}
	
	public static <T> F2<T, T, Integer> getStringSimilarityFunc(final 
		int maxScore, final F<T, String> getFeatureString) {
		return new F2<T, T, Integer>() {
			@Override
			public Integer f(T n0, T n1) {
				Double perc = DlfStringUtils.getSamePartPercentage.f(
					getFeatureString.f(n0), getFeatureString.f(n1));
				return (int)(perc * maxScore);
	}};}
	
	public static <T> F2<T, T, Integer> getCommonWordsStringSimilarityFunc
		(final int maxScore, final F<T, String> getStringFunc) {
		return new F2<T, T, Integer>() {
		@Override
		public Integer f(T n0, T n1) {
			String s0 = getStringFunc.f(n0);
			String s1 = getStringFunc.f(n1);
			return (int)(DlfStringUtils.getCommonWordsPercentage.f(s0, s1) * 
				maxScore);
	}};}
	
	public static <T> F2<List<T>, List<T>, List<P2<T, T>>> 
		getSimilarityMapper(final int minimumScore, final F2<T, T, Integer> 
			similarityScoreFunc) {
		return new F2<List<T>, List<T>, List<P2<T, T>>>(){
			@Override
			public List<P2<T, T>> f(final List<T> list1, final List<T> list2) {
				List<P2<T, T>> multiplied = list1.bind(list2, 
					new F2<T, T, P2<T, T>>(){
					@Override
					public P2<T, T> f(T n1, T n2) {
						return P.p(n1, n2);
				}});
	
				List<P2<T, T>> sorted = multiplied.filter(
					new F<P2<T,T>, Boolean>() {
					@Override
					public Boolean f(P2<T, T> pair) {
						return similarityScoreFunc.f(pair._1(), pair._2()) > 
							minimumScore;
				}}).sort(Ord.intOrd.comap(new F<P2<T, T>, Integer>() {
					@Override
					public Integer f(P2<T, T> p) {
						return similarityScoreFunc.f(p._1(), p._2());
					}
				})).reverse();
				
				List<P2<T, T>> result = List.nil();
				for(;sorted.isNotEmpty();sorted = sorted.tail()) {
					final P2<T, T> head = sorted.head();
					if(result.find(new F<P2<T,T>, Boolean>() {
						@Override
						public Boolean f(P2<T, T> p) {
							return p._1() == head._1() || p._2() == head._2();
					}}).isNone()){
						result = result.snoc(head);
					}
				}
				
				final List<P2<T, T>> currentResult = result;
				List<P2<T, T>> remain1 = list1.filter
					(new F<T, Boolean>() {
					@Override
					public Boolean f(final T node) {
						return currentResult.find(new F<P2<T,T>, 
								Boolean>() {
							@Override
							public Boolean f(P2<T, T> p) {
								return p._1() == node;
							}
						}).isNone();
					}}).map(new F<T, P2<T, T>>() {
					public P2<T, T> f(T p) {
						return P.p(p, null);
					}
				});
				
				List<P2<T, T>> remain2 = list2.filter
					(new F<T, Boolean>() {
					@Override
					public Boolean f(final T node) {
						return currentResult.find(new F<P2<T,T>, 
								Boolean>() {
							@Override
							public Boolean f(P2<T, T> p) {
								return p._2() == node;
							}
						}).isNone();
					}}).map(new F<T, P2<T, T>>() {
					@Override
					public P2<T, T> f(T p) {
						return P.p(null, p);
					}
				});
				result = result.append(remain1).append(remain2);
				return result;
			}};
	}
	
	public static <T, S> F2<List<T>, List<T>, List<P2<T, T>>> 
		getSimilarityMapperByContents(final int threshold, final int maxScore, 
			final Equal<S> contentEq, final F<T, List<S>> getContents){		
		return new F2<List<T>, List<T>, List<P2<T,T>>>() {
			@Override
			public List<P2<T, T>> f(List<T> list1, List<T> list2) {
				final F2<T, T, Integer> getCommonContentesScore = new F2<T, T, 
						Integer>(){
					@Override
					public Integer f(T t0, T t1) {
						List<S> contents1 = getContents.f(t0);
						List<S> contents2 = getContents.f(t1);
						double commonCount = contents1.length() - contents1.minus
							(contentEq, contents2).length();
						double base = (contents1.length() + contents2.length())/2;
						return (int) (commonCount/base * maxScore);
				}};
				final F<T, F<T, Boolean>> alwaysEq = new F<T, F<T,Boolean>>(){
					@Override
					public F<T, Boolean> f(T t) {
						return new F<T, Boolean>() {
							@Override
							public Boolean f(T t1) {
								return true;
				}};}};
				final F2<P2<T, T>, P2<T, T>, Boolean> firstHit = extendEqualToProduct
					(getReferenceEq((T)null), Equal.equal(alwaysEq)).eq();
				final F2<P2<T, T>, P2<T, T>, Boolean> secondHit = extendEqualToProduct
					(Equal.equal(alwaysEq), getReferenceEq((T)null)).eq();
				List<P2<T, T>> sortedPairs = list1.bind(list2, pairFunction
					((T)null)).sort(Ord.intOrd.comap(getCommonContentesScore.
						tuple()));
				List<P2<T, T>> results = List.nil();
				for(; sortedPairs.isNotEmpty(); sortedPairs = sortedPairs.
					drop(1)) {
					P2<T, T> head = sortedPairs.head();
					boolean isFirstInResults = results.exists(firstHit.f(head));
					boolean isSecondInResults = results.exists(secondHit.f(head));
					if(!isFirstInResults && !isSecondInResults)
						results = results.snoc(head);
					else if (!isFirstInResults) {
						results = results.snoc(P.p(head._1(), (T)null));
					}else if (!isSecondInResults) {
						results = results.snoc(P.p((T)null, head._2()));
					}
				}
				return results;
			}
		};
	}
	
	public static <T> F<Option<T>, Boolean> getIsSome(T t) {
		return new F<Option<T>, Boolean>() {
			@Override
			public Boolean f(Option<T> op) {
				return op.isSome();
		}};
	}
	
	public static <T> F<Option<T>, T> getGetSomeFunc(T t) {
		return new F<Option<T>, T>() {
			@Override
			public T f(Option<T> op) {
				return op.some();
		}};
	}

	public static <T> F<Buffer<T>, List<T>> getBuffer2ListFunc(T t) {
		return new F<List.Buffer<T>, List<T>>() {
			@Override
			public List<T> f(Buffer<T> buffer) {
				return buffer.toList();
		}};
	}
}
