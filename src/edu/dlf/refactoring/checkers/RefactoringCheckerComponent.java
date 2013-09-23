package edu.dlf.refactoring.checkers;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import fj.data.HashMap;

public class RefactoringCheckerComponent extends EventBus implements 
		IFactorComponent{

	private final HashMap<RefactoringType, IRefactoringChecker> map;
	
	@Inject
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
	
	@Subscribe
	@Override
	public Void listen(Object event) {
		IDetectedRefactoring refactoring = (IDetectedRefactoring)event;
		IRefactoringChecker checker = this.map.get(refactoring.
				getRefactoringType()).some();
		ICheckingResult result = checker.checkRefactoring(refactoring);
		this.post(result);
		return null;
	}
}
