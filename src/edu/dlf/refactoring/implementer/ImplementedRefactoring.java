package edu.dlf.refactoring.implementer;

import org.eclipse.ltk.core.refactoring.Change;

import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.RefactoringType;

public class ImplementedRefactoring implements 
	IImplementedRefactoring{

	private final RefactoringType type;
	private final Change change;
	
	protected ImplementedRefactoring(RefactoringType type, Change change)
	{
		this.type = type;
		this.change = change;
	}
	
	
	@Override
	public RefactoringType getRefactoringType()
	{
		return this.type;
	}


	@Override
	public Change getChange() {
		return change;
	}
}
