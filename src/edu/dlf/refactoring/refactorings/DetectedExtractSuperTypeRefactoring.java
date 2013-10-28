package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
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

}
