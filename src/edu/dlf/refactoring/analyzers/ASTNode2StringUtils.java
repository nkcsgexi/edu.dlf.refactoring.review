package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;

public class ASTNode2StringUtils {

	private ASTNode2StringUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static F<ASTNode, String> getCorrespondingSource = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			String source = ASTAnalyzer.getOriginalSourceFromRoot(node.getRoot());
			return source.substring(node.getStartPosition(), node.getStartPosition() 
				+ node.getLength());
	}}; 
	
}
