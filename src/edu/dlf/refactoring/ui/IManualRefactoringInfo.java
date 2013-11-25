package edu.dlf.refactoring.ui;

import java.util.Collection;


public interface IManualRefactoringInfo {
	Collection<IManualRefactoringPosition> getLeftRefactoringPostion();
	Collection<IManualRefactoringPosition> getRightRefactoringPostion();
}
