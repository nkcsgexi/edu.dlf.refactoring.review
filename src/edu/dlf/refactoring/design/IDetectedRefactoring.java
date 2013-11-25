package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.data.List;

public interface IDetectedRefactoring extends IRefactoring {
	ASTNode getEffectedNode(SingleNodeDescriptor descriptor);
	List<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor);
	List<SingleNodeDescriptor> getSingleNodeDescriptors();
	List<NodeListDescriptor> getNodeListDescritors();
	List<ASTNode> getEffectedNodesBefore();
	List<ASTNode> getEffectedNodesAfter();
	RefactoringType getRefactoringType();
	
	interface NodesDescriptor{}
	interface SingleNodeDescriptor extends NodesDescriptor{}
	interface NodeListDescriptor extends NodesDescriptor{}
}
