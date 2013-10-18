package edu.dlf.refactoring.design;

import edu.dlf.refactoring.implementer.IImplementedRefactoringCallback;


public interface IRefactoringImplementer {
	void implementRefactoring(IDetectedRefactoring 
			refactoring, IImplementedRefactoringCallback callback);
}
