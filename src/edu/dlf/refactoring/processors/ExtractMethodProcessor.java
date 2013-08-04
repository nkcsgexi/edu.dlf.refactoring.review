package edu.dlf.refactoring.processors;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringAnnotations.ExtractMethod;
import edu.dlf.refactoring.design.RefactoringProcessor;

public class ExtractMethodProcessor extends RefactoringProcessor{

	@Inject
	public ExtractMethodProcessor(@ExtractMethod IRefactoringDetector _refactoringDetector,
			@ExtractMethod IRefactoringChecker _refactoringChecker) {
		super(_refactoringDetector, _refactoringChecker);
	}

	@Override
	public void processRefactoring(ISourceChange change) {
		
	}

}
