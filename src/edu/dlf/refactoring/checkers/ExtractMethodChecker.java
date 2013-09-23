package edu.dlf.refactoring.checkers;
import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import fj.data.Option;

public class ExtractMethodChecker implements IRefactoringChecker{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IRefactoringImplementer implementer;
	
	
	public ExtractMethodChecker(@ExtractMethod IRefactoringImplementer 
			emImplementer)
	{
		this.implementer = emImplementer;
	}
	
	@Override
	public ICheckingResult checkRefactoring(IDetectedRefactoring 
			detectedRefactoring) {
		Option<IImplementedRefactoring> op = implementer.
			implementRefactoring(detectedRefactoring);
		if(op.isSome())
		{
			IImplementedRefactoring implemented = op.some();
			if(isRefactoringSame(implemented, detectedRefactoring))
			{
				return new DefaultCheckingResult(true, detectedRefactoring);
			}
		}
		return new DefaultCheckingResult(false, detectedRefactoring);
	}

	private boolean isRefactoringSame(IImplementedRefactoring implemented,
			IDetectedRefactoring detectedRefactoring) {
		return false;
	}

}
