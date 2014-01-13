package edu.dlf.refactoring.analyzers;

import java.awt.geom.Line2D;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;
import fj.Ord;
import fj.data.List;

public class ASTNode2IntegerUtils {
	
	private ASTNode2IntegerUtils() throws Exception {
		throw new Exception();
	}
	
	public static final F<ASTNode, Integer> getStart = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getStartPosition();
	}};
	
	public static final F<ASTNode, Integer> getEnd = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getStartPosition() + node.getLength() - 1;
	}};
	
	public static final F<ASTNode, Integer> getLength = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getLength();
	}};
	
	public static final F<ASTNode, Integer> getKind = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return node.getNodeType();
	}};
	
	public static final F<ASTNode, Integer> getStartLineNumber = 
		new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return DlfStringUtils.getLinesCount.f(ASTNode2StringUtils.
				getCorrespondingSource.f(node.getRoot()).substring(0, 
					node.getStartPosition() + 1));
	}};

	public static final F<ASTNode, Integer> getLinesSpan = new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			return DlfStringUtils.getLinesCount.f(ASTNode2StringUtils.
				getCorrespondingSource.f(node));
	}};
	
	public static final F<ASTNode, Integer> getEndLineNumber = 
		new F<ASTNode, Integer>() {
		@Override
		public Integer f(ASTNode node) {
			int start = getStartLineNumber.f(node);
			int span = getLinesSpan.f(node);
			return span + start - 1;
	}};
	
	public static final F<ASTNode, Line2D> convertNode2Line = new F<ASTNode, Line2D>() {
		@Override
		public Line2D f(ASTNode node) {
			return new Line2D.Double(getStart.f(node), 0, getEnd.f(node), 0);
	}}; 
	
	public static final F<List<ASTNode>, Integer> getAstNodesLength = 
		new F<List<ASTNode>, Integer>() {
		@Override
		public Integer f(List<ASTNode> nodes) {
			Ord<ASTNode> astNodeStartOrd = Ord.intOrd.comap(getStart);
			Ord<ASTNode> astNodeEndOrd = Ord.intOrd.comap(getEnd);
			int end = getEnd.f(nodes.sort(astNodeEndOrd).last());
			int start = getStart.f(nodes.sort(astNodeStartOrd).head());
			return end - start + 1;
	}};
}
