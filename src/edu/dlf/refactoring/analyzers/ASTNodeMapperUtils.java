package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;

public class ASTNodeMapperUtils {
	private ASTNodeMapperUtils() throws Exception {
		throw new Exception();
	}

	public static F2<ASTNode, ASTNode, Integer> getASTNodeSimilarityFunc(final 
			int maxScore, final F<ASTNode, String> getFeatureString) {
		return FJUtils.getStringSimilarityFunc(maxScore, getFeatureString);
	}

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
			}});
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
		return FJUtils.getCommonWordsStringSimilarityFunc(maxScore, getStringFunc);
	}

	public static F2<ASTNode, ASTNode, Integer> getDefaultASTNodeSimilarityFunc() {
		return new F2<ASTNode, ASTNode, Integer>() {
			@Override
			public Integer f(ASTNode n1, ASTNode n2) {
				return 0 - DlfStringUtils.distance(n1.toString(), n2.toString());
	}};}

	public static F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>>
		getASTNodeMapper(final int minimumScore, final F2<ASTNode, ASTNode, 
			Integer> similarityScoreFunc) {
		return FJUtils.getSimilarityMapper(minimumScore, similarityScoreFunc);
	}
}
