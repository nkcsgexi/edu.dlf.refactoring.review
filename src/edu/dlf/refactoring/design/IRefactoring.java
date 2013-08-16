package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.data.List;

public interface IRefactoring {
	ASTNode getEffectedNode(SingleNodeDescriptor descriptor);
	List<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor);
	RefactoringType getRefactoringType();
	
	interface SingleNodeDescriptor{}
	interface NodeListDescriptor{}
	
	
}
