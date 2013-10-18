package edu.dlf.refactoring.implementer;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;

public interface IImplementedRefactoringCallback {
	void onImplementedRefactoringReady(IDetectedRefactoring detected, 
			IImplementedRefactoring implemented);
}
