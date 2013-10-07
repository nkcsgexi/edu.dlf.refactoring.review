package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import fj.data.Option;

public class RenameMethodChecker implements IRefactoringChecker{

	private final IRefactoringImplementer implementer;
	private final Logger logger;


	@Inject
	public RenameMethodChecker(
			Logger logger,
			@RenameMethod IRefactoringImplementer implementer)
	{
		this.logger = logger;
		this.implementer = implementer;
	}
	
	
	@Override
	public ICheckingResult checkRefactoring(IDetectedRefactoring refactoring) {
		Option<IImplementedRefactoring> implemented = this.implementer.
			implementRefactoring(refactoring);
		if(implemented.isSome())
		{
			IImplementedRefactoring imRefactoring = implemented.some();
			if(isRefactoringSame(imRefactoring, refactoring))
				return new DefaultCheckingResult(true, refactoring);
			else
				return new DefaultCheckingResult(false, refactoring);
		}
		return new DefaultCheckingResult(true, refactoring);
	}


	private boolean isRefactoringSame(IImplementedRefactoring implemented,
		IDetectedRefactoring detected) {
	
	
		return false;
	}

}
