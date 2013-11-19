package edu.dlf.refactoring.analyzers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

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
	
	public static F<ASTNode, String> getCompilationUnitName =new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			return ((CompilationUnit)node.getRoot()).getJavaElement().getElementName();
	}};
	
	public static F<ASTNode, String> getMethodNameFunc = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode method) {
			return method == null ? "" : method.getStructuralProperty
				(MethodDeclaration.NAME_PROPERTY).toString();
	}};
	
	public static F<ASTNode, String> getPreWhitespace = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			ASTNode before = ASTNode2ASTNodeUtils.getASTNodeBefore.f(node);
			if(before == null) return "";
			String source = ASTAnalyzer.getOriginalSourceFromRoot(node.getRoot());
			int start = ASTNode2IntegerUtils.getEnd.f(before) + 1;
			int end = node.getStartPosition() - 1;
			return removeNonWhiteSpace(source.substring(start, end + 1));
	}};
	
	public static F<ASTNode, String> getPostWhitespace = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			ASTNode after = ASTNode2ASTNodeUtils.getASTNodeAfter.f(node);
			if(after == null) return "";
			String source = ASTAnalyzer.getOriginalSourceFromRoot(node.getRoot());
			int start = ASTNode2IntegerUtils.getEnd.f(node) + 1;
			int end = after.getStartPosition() - 1;
			return removeNonWhiteSpace(source.substring(start, end + 1));
	}};
	
	private static String removeNonWhiteSpace(String source)
	{
		if(StringUtils.isBlank(source))
			return source;
		if(source.length() == 1)
			return "";
		return removeNonWhiteSpace(source.substring(0, 1)) + removeNonWhiteSpace
			(source.substring(1));
	}
}
