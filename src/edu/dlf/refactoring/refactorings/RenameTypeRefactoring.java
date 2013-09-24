package edu.dlf.refactoring.refactorings;

import edu.dlf.refactoring.design.RefactoringType;
import fj.data.List;


public class RenameTypeRefactoring extends AbstractRefactoring{

	public RenameTypeRefactoring() {
		super(RefactoringType.RenameType);
	}

	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.nil();
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.nil();
	}


}
