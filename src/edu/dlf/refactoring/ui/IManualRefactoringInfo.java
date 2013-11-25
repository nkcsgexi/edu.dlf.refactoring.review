package edu.dlf.refactoring.ui;

import java.util.Collection;

import edu.dlf.refactoring.design.IRefactoring;


public interface IManualRefactoringInfo extends IRefactoring{
	Collection<IManualRefactoringPosition> getLeftRefactoringPostions();
	Collection<IManualRefactoringPosition> getRightRefactoringPostions();
}
