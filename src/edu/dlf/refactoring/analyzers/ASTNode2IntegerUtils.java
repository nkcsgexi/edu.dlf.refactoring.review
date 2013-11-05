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
	
}
