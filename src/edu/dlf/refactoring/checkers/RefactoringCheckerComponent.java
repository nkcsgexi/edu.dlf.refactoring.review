package edu.dlf.refactoring.checkers;


import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator.UICompAnnotation;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameField;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameLocalVariable;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import edu.dlf.refactoring.implementer.ImplementedRefactoring;
import fj.P2;
import fj.data.HashMap;
import fj.data.Option;

public class RefactoringCheckerComponent implements 
		IFactorComponent{

	private final HashMap<RefactoringType, IRefactoringChecker> map;
	private final EventBus bus;
	private final Logger logger;
	
	@Inject
	public RefactoringCheckerComponent(
			Logger logger,
			@ExtractMethod IRefactoringChecker emChecker,
			@RenameMethod IRefactoringChecker rmChecker,
			@RenameType IRefactoringChecker rtChecker,
			@RenameLocalVariable IRefactoringChecker rlvChecker,
			@RenameField IRefactoringChecker rfChecker,
			@MoveResource IRefactoringChecker mChecker,
			@UICompAnnotation IFactorComponent uiComponent) {
		this.logger = logger;
		this.map = HashMap.hashMap();
		this.map.set(RefactoringType.ExtractMethod, emChecker);
		this.map.set(RefactoringType.RenameMethod, rmChecker);
		this.map.set(RefactoringType.RenameType, rtChecker);
		this.map.set(RefactoringType.RenameLocalVariable, rlvChecker);
		this.map.set(RefactoringType.RenameField, rfChecker);
		this.map.set(RefactoringType.Move, mChecker);
		this.bus = new EventBus();
		//this.bus.register(uiComponent);
	}
	
	@Subscribe
	@Override
	public Void listen(final Object event) {
		if(isEventRight(event)){
			IDetectedRefactoring detected = (IDetectedRefactoring)
				((P2)event)._1();
			IImplementedRefactoring implemented = (ImplementedRefactoring)
				((P2)event)._2();
			Option<IRefactoringChecker> checkOp = map.get(detected.
				getRefactoringType());
			if(checkOp.isSome()) {
				ICheckingResult result = checkOp.some().checkRefactoring
					(detected, implemented);
				bus.post(result);
			} else {
				logger.fatal("Missing checker for " + detected.
					getRefactoringType());
			}
		}
		return null;
	}
	
	private boolean isEventRight(Object event)
	{
		if(event instanceof P2) {
			Object first = ((P2)event)._1();
			Object second = ((P2)event)._2();
			return first instanceof IDetectedRefactoring && second instanceof
				IImplementedRefactoring;
		}
		return false;
	}
	

	@Override
	public Void registerListener(ICompListener listener) {
		this.bus.register(listener);
		return null;
	}
}
