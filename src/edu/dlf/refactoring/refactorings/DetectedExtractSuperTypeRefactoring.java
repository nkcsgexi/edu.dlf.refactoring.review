package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.design.RefactoringType;
import fj.P;
import fj.P2;
import fj.data.List;

public class DetectedExtractSuperTypeRefactoring extends AbstractRefactoring{

	public static NodeListDescriptor TypeReferencesDescriptor = new NodeListDescriptor(){};
	public static SingleNodeDescriptor AddedTypeDeclarationDescriptor = new SingleNodeDescriptor(){};
	
	public DetectedExtractSuperTypeRefactoring(ASTNode declaration, List<ASTNode> 
		typeReferences) {
		super(RefactoringType.ExtractSuperType);
		this.addSingleNode(AddedTypeDeclarationDescriptor, declaration);
		this.addNodeList(TypeReferencesDescriptor, typeReferences);
	}

	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.single(AddedTypeDeclarationDescriptor);
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.single(TypeReferencesDescriptor);
	}

	@Override
	protected List<NodesDescriptor> getBeforeNodesDescriptor() {
		return List.nil();
	}

	@Override
	protected List<NodesDescriptor> getAfterNodesDescriptor() {
		return List.single((NodesDescriptor)TypeReferencesDescriptor).snoc(
			TypeReferencesDescriptor);
	}

	@Override
	protected List<P2<NodesDescriptor, TYPE>> getNodeTypesForCountingDelta() {
		return List.list(
			P.p((NodesDescriptor)TypeReferencesDescriptor, TYPE.CHANGE),
			P.p((NodesDescriptor)AddedTypeDeclarationDescriptor, TYPE.INSERT));
	}

}
