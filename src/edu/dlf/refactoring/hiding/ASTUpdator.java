package edu.dlf.refactoring.hiding;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.XStringUtils;
import fj.F;
import fj.P2;
import fj.data.List;

public class ASTUpdator extends F<ASTNode, ASTNode>{
	
	private final F<ASTNode, P2<String, Boolean>> rewriteRule;
	private final F<ASTNode, List<ASTNode>> getChildrenFunc;
	private final F<ASTNode, String> convertASTNode2String = 
		new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			P2<String, Boolean> updated = rewriteRule.f(node);
			if(updated._2()) return updated._1();
			if(getChildrenFunc.f(node).isEmpty()) return node.toString();
			return getChildrenFunc.f(node).map(convertASTNode2String).
				foldLeft(XStringUtils.stringCombiner, new StringBuilder()).toString();
		}};
	
	public ASTUpdator(F<ASTNode, P2<String, Boolean>> rewriteRule)
	{
		this.rewriteRule = rewriteRule;
		this.getChildrenFunc = ASTAnalyzer.getChildrenFunc;
	}
	
	@Override
	public ASTNode f(ASTNode node) {
		return ASTAnalyzer.parseICompilationUnit(this.convertASTNode2String.
			f(node));
	}
}
