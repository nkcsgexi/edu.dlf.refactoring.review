package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.design.RefactoringType;
import fj.P;
import fj.P2;
import fj.data.List;

public class DetectedInlineMethodRefactoring extends AbstractRefactoring {

	public static final SingleNodeDescriptor methodDeclarationDescriptor = 
		new SingleNodeDescriptor() {};
	public static final NodeListDescriptor methodInvocationsDescriptor = 
		new NodeListDescriptor() {};

	public DetectedInlineMethodRefactoring(ASTNode methodDeclaration, 
			List<ASTNode> methodInvocations) {
		super(RefactoringType.InlineMethod);
		this.addSingleNode(this.methodDeclarationDescriptor, methodDeclaration);
		this.addNodeList(this.methodInvocationsDescriptor, methodInvocations);
	}

	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.single(methodDeclarationDescriptor);
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.single(methodInvocationsDescriptor);
	}

	@Override
	protected List<NodesDescriptor> getBeforeNodesDescriptor() {
		return List.single((NodesDescriptor)methodDeclarationDescriptor).snoc
			(methodInvocationsDescriptor);
	}

	@Override
	protected List<NodesDescriptor> getAfterNodesDescriptor() {
		return List.nil();
	}

	@Override
	protected List<P2<NodesDescriptor, TYPE>> getNodeTypesForCountingDelta() {
		return List.single(P.p((NodesDescriptor)this.methodDeclarationDescriptor, 
			TYPE.DELETE));
	}

}
