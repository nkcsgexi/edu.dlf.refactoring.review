package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

import edu.dlf.refactoring.utils.IEqualityComparer;

public class ASTAnalyzer {

	private ASTAnalyzer() throws Exception {
		throw new Exception();
	}

	public static ASTNode parseICompilationUnit(IJavaElement icu) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource((ICompilationUnit) icu);
		parser.setResolveBindings(true);
		return parser.createAST(null);
	}

	public static ASTNode parseStatements(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		return parser.createAST(null);
	}

	public static ASTNode parseExpression(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		return parser.createAST(null);
	}

	public static boolean areASTNodesSame(ASTNode before, ASTNode after) {
		
		if(before == null && after == null)
			return true;
		if(before == null || after == null)
			return false;
		String bs = XStringUtils.RemoveWhiteSpace(before.toString());
		String as = XStringUtils.RemoveWhiteSpace(after.toString());
		return as.equals(bs);
	}
	
	
	public static IEqualityComparer<ASTNode> getASTEqualityComparer()
	{
		return new IEqualityComparer<ASTNode>(){
			@Override
			public boolean AreEqual(ASTNode a, ASTNode b) {
				return areASTNodesSame(a, b);
			}};
	}

}
