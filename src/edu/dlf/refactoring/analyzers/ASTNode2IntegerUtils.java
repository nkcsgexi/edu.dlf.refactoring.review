package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;

public class ASTNode2IntegerUtils {
	
	private ASTNode2IntegerUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static F<ASTNode, Integer> getStart = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getStartPosition();
	}};
	
	public static F<ASTNode, Integer> getEnd = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getStartPosition() + node.getLength() - 1;
	}};
	
	public static F<ASTNode, Integer> getLength = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getLength();
	}};
	
	public static F<ASTNode, Integer> getKind = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getNodeType();
	}};
	
	public static F<ASTNode, Integer> getStartLineNumber = 
		new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return DlfStringUtils.getLinesCount.f(ASTNode2StringUtils.
				getCorrespondingSource.f(node.getRoot()).substring(0, 
					node.getStartPosition() + 1));
	}};

	public static F<ASTNode, Integer> getLinesSpan = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return DlfStringUtils.getLinesCount.f(ASTNode2StringUtils.
				getCorrespondingSource.f(node));
	}};
	
	public static F<ASTNode, Integer> getEndLineNumber = 
		new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			int start = getStartLineNumber.f(node);
			int span = getLinesSpan.f(node);
			return span + start - 1;
	}};
	
}
