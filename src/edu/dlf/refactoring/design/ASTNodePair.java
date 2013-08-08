package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

public class ASTNodePair {

	private final ASTNode nodeAfter;
	private final ASTNode nodeBefore;

	public ASTNodePair(ASTNode nodeBefore, ASTNode nodeAfter)
	{
		this.nodeBefore = nodeBefore;
		this.nodeAfter = nodeAfter;
	}

	public ASTNode getNodeBefore() {
		return nodeBefore;
	}

	public ASTNode getNodeAfter() {
		return nodeAfter;
	}
}
