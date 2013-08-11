package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import com.google.common.base.Function;

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
	
	public ASTNodePair select(Function<ASTNode, ASTNode> func)
	{
		return new ASTNodePair(func.apply(nodeBefore), 
				func.apply(nodeAfter));
	}
	
	
	public ASTNodePair selectByPropertyDescriptor(final StructuralPropertyDescriptor 
			descriptor)
	{
		return select(new Function<ASTNode,ASTNode>(){
			@Override
			public ASTNode apply(ASTNode node){
				return (ASTNode) node.getStructuralProperty(descriptor);
			}});
	}
}
