package edu.dlf.refactoring.design;

public abstract class RefactoringProcessor {
	
	protected final IRefactoringDetector _refactoringDetector;
	protected final IRefactoringChecker _refactoringChecker;
	
	public RefactoringProcessor(IRefactoringDetector _refactoringDetector, 
			IRefactoringChecker _refactoringChecker)
	{
		this._refactoringDetector = _refactoringDetector;
		this._refactoringChecker = _refactoringChecker;
	}

}
