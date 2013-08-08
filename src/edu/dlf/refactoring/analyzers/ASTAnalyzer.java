package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class ASTAnalyzer {

	private ASTAnalyzer() throws Exception
	{
		throw new Exception();
	}
	
	public static ASTNode parseICompilationUnit(IJavaElement icu)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
	    parser.setKind(ASTParser.K_COMPILATION_UNIT);
	    parser.setSource((ICompilationUnit)icu);
	    parser.setResolveBindings(true);
	    return parser.createAST(null);
	}
	
	public static ASTNode parseStatements(String source)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		return parser.createAST(null);
	}
	 
	public static ASTNode parseExpression(String source)
	{
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		return parser.createAST(null);
	}
	
	
	
}
