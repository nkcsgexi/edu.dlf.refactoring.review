package edu.dlf.refactoring.design;

import edu.dlf.refactoring.checkers.ICheckingResult;

public interface IRefactoringChecker {
	ICheckingResult checkRefactoring(IDetectedRefactoring refactoring);
}
