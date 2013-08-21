package edu.dlf.refactoring.checkers;


import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import fj.data.HashMap;

public class RefactoringCheckerComponent implements IFactorComponent{

	private final HashMap<RefactoringType, IRefactoringChecker> map;
	
	public RefactoringCheckerComponent(
			@ExtractMethod IRefactoringChecker emChecker,
			@RenameMethod IRefactoringChecker rmChecker,
			@RenameType IRefactoringChecker rtChecker)
	{
		this.map = HashMap.hashMap();
		this.map.set(RefactoringType.ExtractMethod, emChecker);
		this.map.set(RefactoringType.RenameMethod, rmChecker);
		this.map.set(RefactoringType.RenameType, rtChecker);
	}
	
	
	@Override
	public Void listen(Object event) {
		IDetectedRefactoring refactoring = (IDetectedRefactoring)event;
		IRefactoringChecker checker = this.map.get(refactoring.
				getRefactoringType()).some();
		checker.checkRefactoring(refactoring);
		return null;
	}

}
