package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringCheckerCompAnnotation;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameField;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameLocalVariable;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import fj.P;
import fj.data.HashMap;
import fj.data.Option;

public class RefactoringImplementerComponent implements IFactorComponent, 
	IImplementedRefactoringCallback{
	
	private final Logger logger;
	
	private final HashMap<RefactoringType, IRefactoringImplementer> 
		implementers;

	private final EventBus bus;

	@Inject
	public RefactoringImplementerComponent(
			Logger logger,
			@ExtractMethod final IRefactoringImplementer emImplementer,
			@RenameMethod final IRefactoringImplementer rmImplementer,
			@RenameType final IRefactoringImplementer rtImplementer,
			@RenameLocalVariable final IRefactoringImplementer rlvImplementer,
			@RenameField final IRefactoringImplementer rfImplemented,
			@MoveResource final IRefactoringImplementer mvImplementer,
			@RefactoringCheckerCompAnnotation final IFactorComponent nextComp) {
		this.logger = logger;
		this.implementers = HashMap.hashMap();
		this.implementers.set(RefactoringType.ExtractMethod, emImplementer);
		this.implementers.set(RefactoringType.RenameMethod, rmImplementer);
		this.implementers.set(RefactoringType.RenameType, rtImplementer);
		this.implementers.set(RefactoringType.RenameLocalVariable, rlvImplementer);
		this.implementers.set(RefactoringType.RenameField, rfImplemented);
		this.implementers.set(RefactoringType.Move, mvImplementer);
		this.bus = new EventBus();
		this.bus.register(nextComp);
	}
	
	
	@Override
	public Void listen(final Object event) {
		if(event instanceof IDetectedRefactoring){
			logger.info("Implementation starts.");
			final IImplementedRefactoringCallback listener = this;
			try {
				IDetectedRefactoring refactoring = (IDetectedRefactoring)event;
				Option<IRefactoringImplementer> implementer = implementers.
					get(((IDetectedRefactoring)event).getRefactoringType());
				if(implementer.isSome())
					implementer.some().implementRefactoring(refactoring, listener);
			} catch (Exception e) {
				logger.fatal(e);
			}
			logger.info("Implementation ends.");
		};
		return null;
	}


	@Override
	public Void registerListener(ICompListener listener) {
		return null;
	}


	@Override
	public void onImplementedRefactoringReady(IDetectedRefactoring detected,
			IImplementedRefactoring implemented) {
		bus.post(P.p(detected, implemented));
	}
}
