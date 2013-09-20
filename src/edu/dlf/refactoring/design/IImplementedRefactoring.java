package edu.dlf.refactoring.design;

import org.eclipse.ltk.core.refactoring.Change;

public interface IImplementedRefactoring extends IRefactoring{
	RefactoringType getRefactoringType();
	Change getChange();
}
