package edu.dlf.refactoring.refactorings;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.utils.XList;

abstract class AbstractRefactoring implements IRefactoring
{
	private final HashMap<SingleNodeDescriptor, ASTNode> singleNodes;
	private final HashMap<NodeListDescriptor, XList<ASTNode>> nodeLists;
	private final RefactoringType refactoringType;
	
	protected AbstractRefactoring(RefactoringType refactoringType)
	{
		this.refactoringType = refactoringType;
		this.singleNodes = new HashMap<SingleNodeDescriptor, ASTNode>();
		this.nodeLists = new HashMap<NodeListDescriptor, XList<ASTNode>>();
	}
	
	protected void addSingleNode(SingleNodeDescriptor descriptor, ASTNode node)
	{
		this.singleNodes.put(descriptor, node);
	}
	
	protected void addNodeList(NodeListDescriptor decriptor, XList<ASTNode> list)
	{
		this.nodeLists.put(decriptor, list);
	}
	
	@Override
	public ASTNode getEffectedNode(SingleNodeDescriptor descriptor) {
		return this.singleNodes.get(descriptor);
	}
	
	@Override
	public XList<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor) {
		return this.nodeLists.get(descriptor);
	}
	
	@Override
	public RefactoringType getRefactoringType()
	{
		return this.refactoringType;
	}
	
}