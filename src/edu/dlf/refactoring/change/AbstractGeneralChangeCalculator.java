package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.analyzers.ASTNode2IntegerUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public abstract class AbstractGeneralChangeCalculator implements 
	IASTNodeChangeCalculator{

	protected final F<P2<ASTNode, ASTNode>, Boolean> areBothNotNull = 
		new F<P2<ASTNode,ASTNode>, Boolean>() {
		@Override
		public Boolean f(P2<ASTNode, ASTNode> p) {
			return p._1() != null && p._2() != null;
	}}; 
	
	private final F<ASTNode, Boolean> isExpression = new F<ASTNode, Boolean>() {
		@Override
		public Boolean f(ASTNode node) {
			return node instanceof Expression;
	}};
	
	private final F<ASTNode, Boolean> isStatement = new F<ASTNode, Boolean>() {
		@Override
		public Boolean f(ASTNode node) {
			return node instanceof Statement;
	}};
	
	protected final F<ASTNode, List<ASTNode>> getDecendantExpressions = ASTAnalyzer.
		getAllDecendantsFunc.andThen(new F<List<ASTNode>, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(List<ASTNode> decendants) {
				return decendants.filter(isExpression);
	}});
	
	protected final F<ASTNode, List<ASTNode>> getDecendantStatements = ASTAnalyzer.
		getAllDecendantsFunc.andThen(new F<List<ASTNode>, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(List<ASTNode> decendants) {
				return decendants.filter(isStatement);
	}});
		
	protected final Equal<ASTNode> typeEq = Equal.intEqual.comap
		(ASTNode2IntegerUtils.getKind);
		
	protected final Equal<List<ASTNode>> listTypeEq = typeEq.comap(FJUtils.
		getHeadFunc((ASTNode)null));
		
	protected final F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> 
		similarNodeMapper = ASTAnalyzer.getASTNodeMapper(6, ASTAnalyzer.
			getDefaultASTNodeSimilarityScore(10));
	
	private final Ord<P2<ASTNode, ASTNode>> orderByCombinedASTNodeLength = Ord.
		intOrd.comap(new F<P2<ASTNode, ASTNode>, Integer>() {
			@Override
			public Integer f(P2<ASTNode, ASTNode> pair) {
				return ASTNode2IntegerUtils.getLength.f(pair._1()) + 
					ASTNode2IntegerUtils.getLength.f(pair._2()); 
	}});
	
	protected final Ord<P2<ASTNode, ASTNode>> orderByFirstNodeStart = Ord.intOrd.
		comap(FJUtils.getFirstElementInPFunc((ASTNode)null, (ASTNode)null).
			andThen(ASTNode2IntegerUtils.getStart));
	
	private final F2<P2<ASTNode, ASTNode>, P2<ASTNode, ASTNode>, Boolean> 
		isFirstContainedBySecond = new F2<P2<ASTNode,ASTNode>, P2<ASTNode,ASTNode>, 
			Boolean>() {
			@Override
			public Boolean f(P2<ASTNode, ASTNode> p0, P2<ASTNode, ASTNode> p1) {
				return ASTNode2Boolean.isFirstAncestorOfSecond.f(p1._1(), p0._1()) 
					||ASTNode2Boolean.isFirstAncestorOfSecond.f(p1._2(), p0._2());
	}};
	
	protected List<P2<ASTNode,ASTNode>> removeSubPairs(List<P2<ASTNode, ASTNode>> 
			list) {
		list = list.sort(orderByCombinedASTNodeLength);
		Buffer<P2<ASTNode, ASTNode>> result = Buffer.empty();
		while(list.isNotEmpty()) {
			P2<ASTNode, ASTNode> head = list.head();
			list = list.drop(1);
			if(list.find(isFirstContainedBySecond.f(head)).isNone()) {
				result.snoc(head);
			}
		}
		return result.toList();
	}

	
	protected SubChangeContainer pruneSourceChangeContainer(SubChangeContainer 
		container) {
		return (SubChangeContainer) SourceChangeUtils.pruneSourceChange(container);
	}
}
