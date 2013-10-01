package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

public interface IASTNodePair
{
	public ASTNode getNodeBefore();
	public ASTNode getNodeAfter();
}

