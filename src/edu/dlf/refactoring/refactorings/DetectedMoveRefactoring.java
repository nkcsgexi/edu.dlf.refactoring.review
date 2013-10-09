package edu.dlf.refactoring.refactorings;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.data.List;

public class DetectedMoveRefactoring extends AbstractRefactoring{

	public static SingleNodeDescriptor RemovedDeclarationDescriptor = 
		new SingleNodeDescriptor(){};
	public static SingleNodeDescriptor AddedDeclarationDescripter = 
		new SingleNodeDescriptor(){};
	
	private Logger logger = ServiceLocator.ResolveType(Logger.class);	
		
	public DetectedMoveRefactoring(RefactoringType refactoringType, ASTNode removedNode,
			ASTNode addedNode) {
		super(refactoringType);
		this.addSingleNode(AddedDeclarationDescripter, addedNode);
		this.addSingleNode(RemovedDeclarationDescriptor, removedNode);
		logger.info(refactoringType + " created.");
	}

	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.single(RemovedDeclarationDescriptor).snoc(
			AddedDeclarationDescripter);
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.nil();
	}

}
