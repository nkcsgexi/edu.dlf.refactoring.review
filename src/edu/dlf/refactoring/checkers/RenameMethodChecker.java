package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;

public class RenameMethodChecker implements IRefactoringChecker{

	private final Logger logger;

	@Inject
	public RenameMethodChecker(Logger logger)
	{
		this.logger = logger;
	}
	
	
	@Override
	public synchronized ICheckingResult checkRefactoring(IDetectedRefactoring 
		detected, IImplementedRefactoring implemented) {
			if(isRefactoringSame(implemented, detected))
				return new DefaultCheckingResult(true, detected);
			else
				return new DefaultCheckingResult(false, detected);
	}


	private boolean isRefactoringSame(IImplementedRefactoring implemented,
		IDetectedRefactoring detected) {
	
	
		return true;
	}

}
