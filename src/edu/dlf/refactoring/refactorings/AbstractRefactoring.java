package edu.dlf.refactoring.refactorings;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.RefactoringType;
import fj.F;
import fj.data.List;

abstract class AbstractRefactoring implements IDetectedRefactoring
{

	protected abstract List<NodesDescriptor> getBeforeNodesDescriptor();
	protected abstract List<NodesDescriptor> getAfterNodesDescriptor();
	
	private final HashMap<SingleNodeDescriptor, ASTNode> singleNodes;
	private final HashMap<NodeListDescriptor, List<ASTNode>> nodeLists;
	private final RefactoringType refactoringType;
	
	protected AbstractRefactoring(RefactoringType refactoringType)
	{
		this.refactoringType = refactoringType;
		this.singleNodes = new HashMap<SingleNodeDescriptor, ASTNode>();
		this.nodeLists = new HashMap<NodeListDescriptor, List<ASTNode>>();
	}
	
	protected void addSingleNode(SingleNodeDescriptor descriptor, ASTNode node)
	{
		this.singleNodes.put(descriptor, node);
	}
	
	protected void addNodeList(NodeListDescriptor decriptor, List<ASTNode> list)
	{
		this.nodeLists.put(decriptor, list);
	}
	
	final F<NodesDescriptor, List<ASTNode>> getNodes = 
		new F<NodesDescriptor, List<ASTNode>>() {
		@Override
		public List<ASTNode> f(NodesDescriptor descriptor) {
			if(descriptor instanceof SingleNodeDescriptor)
				return List.single(getEffectedNode((SingleNodeDescriptor)descriptor));
			else 
				return getEffectedNodeList((NodeListDescriptor)descriptor);
	}}; 
	
	
	@Override
	public List<ASTNode> getEffectedNodesBefore() {
		return getBeforeNodesDescriptor().bind(getNodes);
	}
	

	@Override
	public List<ASTNode> getEffectedNodesAfter() {
		return getAfterNodesDescriptor().bind(getNodes);
	}
	
	@Override
	public ASTNode getEffectedNode(SingleNodeDescriptor descriptor) {
		return this.singleNodes.get(descriptor);
	}
	
	@Override
	public List<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor) {
		return this.nodeLists.get(descriptor);
	}
	
	@Override
	public RefactoringType getRefactoringType() {
		return this.refactoringType;
	}
}