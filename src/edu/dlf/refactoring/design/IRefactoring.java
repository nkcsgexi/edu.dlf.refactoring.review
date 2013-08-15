package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.utils.XList;

public interface IRefactoring {
	ASTNode getEffectedNode(SingleNodeDescriptor descriptor);
	XList<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor);
	
	interface SingleNodeDescriptor{}
	
	interface NodeListDescriptor{}
}
