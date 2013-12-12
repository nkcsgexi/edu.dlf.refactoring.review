package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import fj.P2;
import fj.data.List;

public interface IDetectedRefactoring extends IRefactoring {
	ASTNode getEffectedNode(SingleNodeDescriptor descriptor);
	List<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor);
	List<SingleNodeDescriptor> getSingleNodeDescriptors();
	List<NodeListDescriptor> getNodeListDescritors();
	List<ASTNode> getEffectedNodesBefore();
	List<ASTNode> getEffectedNodesAfter();
	List<P2<TYPE, Integer>> getDeltaSummary();
	RefactoringType getRefactoringType();
	
	interface NodesDescriptor{}
	interface SingleNodeDescriptor extends NodesDescriptor{}
	interface NodeListDescriptor extends NodesDescriptor{}
}
