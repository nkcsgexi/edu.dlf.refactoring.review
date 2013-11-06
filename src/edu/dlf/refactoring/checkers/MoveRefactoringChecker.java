package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;

public class MoveRefactoringChecker implements IRefactoringChecker{

	private final Logger logger;

	@Inject
	public MoveRefactoringChecker(Logger logger)
	{
		this.logger = logger;
	}
	
	
	@Override
	public ICheckingResult checkRefactoring(final IDetectedRefactoring 
			detectedRefactoring, final IImplementedRefactoring implemented) {
		logger.info("Checking move.");
		return new ICheckingResult() {
			
			@Override
			public IDetectedRefactoring getDetectedRefactoring() {
				return detectedRefactoring;
			}
			
			@Override
			public boolean IsBehaviorPreserving() {
				return true;
			}
		};
	}

}
