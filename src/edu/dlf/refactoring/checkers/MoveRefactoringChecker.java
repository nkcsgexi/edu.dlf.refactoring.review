package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import fj.data.Option;

public class MoveRefactoringChecker implements IRefactoringChecker{

	private final Logger logger;
	private final IRefactoringImplementer implementer;

	@Inject
	public MoveRefactoringChecker(Logger logger,
			@MoveResource IRefactoringImplementer implementer)
	{
		this.logger = logger;
		this.implementer = implementer;
	}
	
	
	@Override
	public ICheckingResult checkRefactoring(IDetectedRefactoring refactoring) {
		Option<IImplementedRefactoring> im = this.implementer.implementRefactoring
			(refactoring);
		if(im.isSome())
		{
			
		}
		
		
		
		
		
		
		
		return null;
	}

}
