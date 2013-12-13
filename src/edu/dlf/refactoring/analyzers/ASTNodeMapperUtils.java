package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;

public class ASTNodeMapperUtils {
	private ASTNodeMapperUtils() throws Exception {
		throw new Exception();
	}

	public static F2<ASTNode, ASTNode, Integer> getASTNodeSimilarityFunc(final 
			int maxScore, final F<ASTNode, String> getFeatureString) {
			return new F2<ASTNode, ASTNode, Integer>() {
				@Override
				public Integer f(ASTNode n0, ASTNode n1) {
					Double perc = DlfStringUtils.getSamePartPercentage.f(
						getFeatureString.f(n0), getFeatureString.f(n1));
					return (int)(perc * maxScore);
	}};}

	public static List<P2<ASTNode, ASTNode>> getSameNodePairs(List<ASTNode> list1, 
		List<ASTNode> list2, final F2<ASTNode, ASTNode, Boolean> areSame) {
		return list1.bind(list2, new F2<ASTNode, ASTNode, P2<ASTNode, ASTNode>>(){
			@Override
			public P2<ASTNode, ASTNode> f(ASTNode n1, ASTNode n2) {
				return P.p(n1, n2);
			}}).filter(new F<P2<ASTNode,ASTNode>, Boolean>() {
				@Override
				public Boolean f(P2<ASTNode, ASTNode> pair) {
					return areSame.f(pair._1(), pair._2());
				}
			});
	}

	public static F2<ASTNode, ASTNode, Integer> getASTNodeSimilarityFunc(final 
		int maxScore) {
		return new F2<ASTNode, ASTNode, Integer>() {
			@Override
			public Integer f(ASTNode n0, ASTNode n1) {
				Double perc = DlfStringUtils.getSamePartPercentage.f(n0.toString(), 
					n1.toString());
				return (int)(perc * maxScore);
		}};
	}

	public static F2<ASTNode, ASTNode, Integer> getCommonWordsASTNodeSimilarityScoreFunc
		(final int maxScore, final F<ASTNode, String> getStringFunc) {
		return new F2<ASTNode, ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode n0, ASTNode n1) {
			String s0 = getStringFunc.f(n0);
			String s1 = getStringFunc.f(n1);
			return (int)(DlfStringUtils.getCommonWordsPercentage.f(s0, s1) * 
				maxScore);
	}};}

	public static F2<ASTNode, ASTNode, Integer> getDefaultASTNodeSimilarityFunc() {
		return new F2<ASTNode, ASTNode, Integer>() {
			@Override
			public Integer f(ASTNode n1, ASTNode n2) {
				return 0 - DlfStringUtils.distance(n1.toString(), n2.toString());
		}};
	}

	public static F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>>
		getASTNodeMapper(final int minimumScore, final F2<ASTNode, ASTNode, 
			Integer> similarityScoreFunc)
	{
		return new F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>>(){
			@Override
			public List<P2<ASTNode, ASTNode>> f(final List<ASTNode> list1, 
				final List<ASTNode> list2) {
				
				List<P2<ASTNode, ASTNode>> multiplied = list1.bind(list2, 
					new F2<ASTNode, ASTNode, P2<ASTNode, ASTNode>>(){
					@Override
					public P2<ASTNode, ASTNode> f(ASTNode n1, ASTNode n2) {
						return P.p(n1, n2);
					}});
	
				List<P2<ASTNode, ASTNode>> sorted = multiplied.filter(
					new F<P2<ASTNode,ASTNode>, Boolean>() {
					@Override
					public Boolean f(P2<ASTNode, ASTNode> pair) {
						return similarityScoreFunc.f(pair._1(), pair._2()) > 
							minimumScore;
					}
				}).sort(Ord.intOrd.comap(new F<P2<ASTNode, ASTNode>, Integer>() {
					@Override
					public Integer f(P2<ASTNode, ASTNode> p) {
						return similarityScoreFunc.f(p._1(), p._2());
					}
				})).reverse();
				
				List<P2<ASTNode, ASTNode>> result = List.nil();
				for(;sorted.isNotEmpty();sorted = sorted.tail()) {
					final P2<ASTNode, ASTNode> head = sorted.head();
					if(result.find(new F<P2<ASTNode,ASTNode>, Boolean>() {
						@Override
						public Boolean f(P2<ASTNode, ASTNode> p) {
							return p._1() == head._1() || p._2() == head._2();
						}
					}).isNone()){
						result = result.snoc(head);
					}
				}
				
				final List<P2<ASTNode, ASTNode>> currentResult = result;
				List<P2<ASTNode, ASTNode>> remain1 = list1.filter
					(new F<ASTNode, Boolean>() {
					@Override
					public Boolean f(final ASTNode node) {
						return currentResult.find(new F<P2<ASTNode,ASTNode>, 
								Boolean>() {
							@Override
							public Boolean f(P2<ASTNode, ASTNode> p) {
								return p._1() == node;
							}
						}).isNone();
					}
				}).map(new F<ASTNode, P2<ASTNode, ASTNode>>() {
					@Override
					public P2<ASTNode, ASTNode> f(ASTNode p) {
						return P.p(p, null);
					}
				});
				
				List<P2<ASTNode, ASTNode>> remain2 = list2.filter
					(new F<ASTNode, Boolean>() {
					@Override
					public Boolean f(final ASTNode node) {
						return currentResult.find(new F<P2<ASTNode,ASTNode>, 
								Boolean>() {
							@Override
							public Boolean f(P2<ASTNode, ASTNode> p) {
								return p._2() == node;
							}
						}).isNone();
					}}).map(new F<ASTNode, P2<ASTNode, ASTNode>>() {
					@Override
					public P2<ASTNode, ASTNode> f(ASTNode p) {
						return P.p(null, p);
					}
				});
				result = result.append(remain1).append(remain2);
				return result;
			}};
	}
}
