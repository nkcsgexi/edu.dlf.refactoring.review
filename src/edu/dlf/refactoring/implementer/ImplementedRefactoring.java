package edu.dlf.refactoring.implementer;

import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import fj.data.List;

public class ImplementedRefactoring implements IImplementedRefactoring{

	private final RefactoringType type;
	private final List<ISourceChange> changes;
	
	protected ImplementedRefactoring(RefactoringType type, List<ISourceChange> 
		changes)
	{
		this.type = type;
		this.changes = changes;
	}
	
	
	@Override
	public RefactoringType getRefactoringType()
	{
		return this.type;
	}


	@Override
	public List<ISourceChange> getSourceChanges() {
		return this.changes;
	}
}
