package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.ICheckingResult;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;

public class ExtractMethodChecker implements IRefactoringChecker{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	
	public ExtractMethodChecker(@ExtractMethod IRefactoringImplementer 
			emImplementer)
	{
		
	}
	
	
	@Override
	public ICheckingResult checkRefactoring(IDetectedRefactoring refactoring) {

		
		
		
		return null;
	}

}
