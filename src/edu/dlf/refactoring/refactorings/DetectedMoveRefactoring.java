package edu.dlf.refactoring.refactorings;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.P;
import fj.P2;
import fj.data.List;

public final class DetectedMoveRefactoring extends AbstractRefactoring{

	public static SingleNodeDescriptor RemovedDeclarationDescriptor = 
		new SingleNodeDescriptor(){};
	public static SingleNodeDescriptor AddedDeclarationDescripter = 
		new SingleNodeDescriptor(){};
	
	private Logger logger = ServiceLocator.ResolveType(Logger.class);	
		
	public DetectedMoveRefactoring(RefactoringType refactoringType, ASTNode removedNode,
			ASTNode addedNode) {
		super(RefactoringType.Move);
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

	@Override
	protected List<NodesDescriptor> getBeforeNodesDescriptor() {
		return List.single((NodesDescriptor)RemovedDeclarationDescriptor);
	}

	@Override
	protected List<NodesDescriptor> getAfterNodesDescriptor() {
		return List.single((NodesDescriptor)AddedDeclarationDescripter);
	}

	@Override
	protected List<P2<NodesDescriptor, TYPE>> getNodeTypesForCountingDelta() {
		return List.list(
			P.p((NodesDescriptor)RemovedDeclarationDescriptor, TYPE.DELETE), 
			P.p((NodesDescriptor)AddedDeclarationDescripter, TYPE.INSERT));
	}
	
	public IJavaElement getBeforeLocation() {
		return ((CompilationUnit)this.getEffectedNode(RemovedDeclarationDescriptor).
			getRoot()).getJavaElement();
	}
	
	public IJavaElement getAfterLocation() {
		return ((CompilationUnit)this.getEffectedNode(AddedDeclarationDescripter).
			getRoot()).getJavaElement();
	}
}
