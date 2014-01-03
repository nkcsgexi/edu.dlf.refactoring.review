package edu.dlf.refactoring.analyzers;

import java.awt.geom.Line2D;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;
import fj.F2;

public class ASTNode2Boolean {
	private ASTNode2Boolean() throws Exception {
		throw new Exception();
	}
	
	public static final F2<ASTNode, ASTNode, Boolean> isFirstAncestorOfSecond =
		new F2<ASTNode, ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode first, ASTNode second) {
				F<ASTNode, Boolean> finder = FJUtils.getReferenceEq
					((ASTNode)null).eq(second);
				return ASTAnalyzer.getAllDecendantsFunc.f(first).find(finder).
					isSome();
	}};
	
	public static final F2<ASTNode, Integer, Boolean> isASTNodeTypeRight = 
		new F2<ASTNode, Integer, Boolean>() {
			@Override
			public Boolean f(ASTNode node, Integer type) {
				return node.getNodeType() == type;
	}};
	
	public static final F2<ASTNode, ASTNode, Boolean> areASTNodesNeighbors = 
		new F2<ASTNode, ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode node1, ASTNode node2) {
				if(araAstNodesOverlapped.f(node1, node2))
					return false;
				if(node1.getRoot() != node2.getRoot())
					return false;
				ASTNode beforeNode = node1.getStartPosition() < node2.
					getStartPosition() ? node1 : node2;
				ASTNode afterNode = node1 == beforeNode ? node2 : node1; 
				final int gapStart = ASTNode2IntegerUtils.getEnd.f(beforeNode) + 1;
				final int gapEnd = afterNode.getStartPosition() - 1;
				ASTNode parent = ASTNode2ASTNodeUtils.getClosestCommonParent.
					f(node1, node2);
				return ASTAnalyzer.getAllDecendantsFunc.f(parent).find(
					new F<ASTNode, Boolean>() {
					@Override
					public Boolean f(ASTNode n) {
						return n.getStartPosition() >= gapStart && 
							ASTNode2IntegerUtils.getEnd.f(n) <= gapEnd;
				}}).isNone();
	}};
	
	
	public static final F2<ASTNode, ASTNode, Boolean> araAstNodesOverlapped =
		new F2<ASTNode, ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode node0, ASTNode node1) {
				Line2D l0 = ASTNode2IntegerUtils.convertNode2Line.f(node0);
				Line2D l1 = ASTNode2IntegerUtils.convertNode2Line.f(node1);
				return l0.intersectsLine(l1);
	}};
	
}
