package edu.dlf.refactoring.processors;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringAnnotations.RenameType;
import edu.dlf.refactoring.design.RefactoringProcessor;

public class RenameTypeProcessor extends RefactoringProcessor{

	@Inject
	public RenameTypeProcessor(@RenameType IRefactoringDetector _refactoringDetector,
			@RenameType IRefactoringChecker _refactoringChecker) {
		super(_refactoringDetector, _refactoringChecker);
	}

	@Subscribe
	public void processRefactoring(ISourceChange refactoring) {
		System.out.println("source");
		
	}

}
