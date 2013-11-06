package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import fj.F;
import fj.Ord;
import fj.data.List;

public class ASTNode2ASTNodeUtils {
	
	private ASTNode2ASTNodeUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static F<ASTNode, List<ASTNode>> getAncestorStatement = 
		ASTAnalyzer.getAncestorsConditionalFunc.f(new F<ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode node) {
				return ASTAnalyzer.isStatement(node);
	}});
	
	public static F<ASTNode, List<ASTNode>> getMethodStatements = 
		new F<ASTNode, List<ASTNode>>() {
		@Override
		public List<ASTNode> f(ASTNode method) {
			ASTNode body = (ASTNode) method.getStructuralProperty(MethodDeclaration.BODY_PROPERTY);
			return FunctionalJavaUtil.createListFromCollection((java.util.List)body.
					getStructuralProperty(Block.STATEMENTS_PROPERTY));
	}};
	
	public static F<ASTNode, ASTNode> getEnclosingStatement = new F<ASTNode,ASTNode>(){
		@Override
		public ASTNode f(ASTNode node) {
			return ASTNode2ASTNodeUtils.getAncestorStatement.f(node).sort
					(Ord.intOrd.comap(ASTNode2IntegerUtils.getLength)).head();
	}};
}
