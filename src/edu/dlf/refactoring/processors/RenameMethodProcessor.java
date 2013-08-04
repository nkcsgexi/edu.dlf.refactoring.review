package edu.dlf.refactoring.processors;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringAnnotations.RenameMethod;
import edu.dlf.refactoring.design.RefactoringProcessor;

public class RenameMethodProcessor extends RefactoringProcessor{

	@Inject
	public RenameMethodProcessor(@RenameMethod IRefactoringDetector _refactoringDetector,
			@RenameMethod IRefactoringChecker _refactoringChecker) {
		super(_refactoringDetector, _refactoringChecker);
	}

	@Override
	public void processRefactoring(ISourceChange change) {
		
	}

}
