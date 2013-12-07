package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.Statement;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2IntegerUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P2;
import fj.data.List;

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
	
	protected final F<ASTNode, List<ASTNode>> getDecExpressions = ASTAnalyzer.
		getAllDecendantsFunc.andThen(new F<List<ASTNode>, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(List<ASTNode> decendants) {
				return decendants.filter(isExpression);
	}});
	
	protected final F<ASTNode, List<ASTNode>> getDecStatements = ASTAnalyzer.
		getAllDecendantsFunc.andThen(new F<List<ASTNode>, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(List<ASTNode> decendants) {
				return decendants.filter(isExpression);
	}});
		
	protected final Equal<ASTNode> typeEq = Equal.intEqual.comap
		(ASTNode2IntegerUtils.getKind);
		
	protected final Equal<List<ASTNode>> listTypeEq = typeEq.comap(FJUtils.
		getHeadFunc((ASTNode)null));
		
	protected final F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> 
		similarNodeMapper = ASTAnalyzer.getASTNodeMapper(6, ASTAnalyzer.
			getDefaultASTNodeSimilarityScore(10));
	
	protected final Ord<P2<ASTNode, ASTNode>> orderByFirstASTNodeLength = Ord.
		intOrd.comap(FJUtils.getFirstElementInPFunc((ASTNode)null, 
			(ASTNode)null).andThen(ASTNode2IntegerUtils.getLength));
	
	
}
