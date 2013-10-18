package edu.dlf.refactoring.checkers;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;

public class RenameTypeChecker implements IRefactoringChecker{

	@Override
	public ICheckingResult checkRefactoring(IDetectedRefactoring detected,
			IImplementedRefactoring implemented) {
		return new DefaultCheckingResult(true, detected);
	}

}
