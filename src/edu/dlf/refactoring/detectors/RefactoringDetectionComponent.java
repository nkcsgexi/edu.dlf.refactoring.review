package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import fj.Effect;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class RefactoringDetectionComponent implements IFactorComponent{
	
	private final List<IRefactoringDetector> detectors;
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);

	@Inject
	public RefactoringDetectionComponent(
			@RenameMethod IRefactoringDetector rmDetector,
			@ExtractMethod IRefactoringDetector emDetector,
			@RenameType IRefactoringDetector rtDetector)
	{
		Buffer<IRefactoringDetector> buffer = Buffer.empty();
		buffer.snoc(rmDetector);
		buffer.snoc(emDetector);
		buffer.snoc(rtDetector);
		this.detectors = buffer.toList();
	}

	@Subscribe
	@Override
	public Void listen(Object event) {
		if(event instanceof ISourceChange)
		{
			logger.info("Refactoring dection component gets event.");
			final ISourceChange change = (ISourceChange) event;
			detectors.bind(new F<IRefactoringDetector, 
					List<IRefactoring>>(){
				@Override
				public List<IRefactoring> f(IRefactoringDetector d) {
					return d.detectRefactoring(change);
				}}).foreach(new Effect<IRefactoring>(){
					@Override
					public void e(IRefactoring arg0) {
						EventBus bus = ServiceLocator.ResolveType(EventBus.class);
						bus.post(arg0);
					}});
		}
		return null;
	}
}
