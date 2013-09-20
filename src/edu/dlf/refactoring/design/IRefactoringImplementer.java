package edu.dlf.refactoring.design;

import fj.data.Option;

public interface IRefactoringImplementer {
	Option<IImplementedRefactoring> implementRefactoring(IDetectedRefactoring 
			refactoring);
}
