package edu.dlf.refactoring.design;

import fj.data.List;


public interface IImplementedRefactoring extends IRefactoring{
	RefactoringType getRefactoringType();
	List<ISourceChange> getSourceChanges();
}
