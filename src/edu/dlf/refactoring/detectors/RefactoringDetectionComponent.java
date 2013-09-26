package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringCheckerCompAnnotation;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import fj.Effect;
import fj.F;
import fj.data.List;
import static fj.data.List.list;  

public class RefactoringDetectionComponent implements IFactorComponent{
	
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final List<IRefactoringDetector> detectorsList;
	private final EventBus bus;

	@Inject
	public RefactoringDetectionComponent(
			@RenameMethod IRefactoringDetector rmDetector,
			@ExtractMethod IRefactoringDetector emDetector,
			@RenameType IRefactoringDetector rtDetector,
			@RefactoringCheckerCompAnnotation IFactorComponent component)
	{
		this.detectorsList = list(rmDetector, emDetector, rtDetector);
		this.bus = new EventBus();
		bus.register(component);
	}
	
	@Subscribe
	@Override
	public Void listen(Object event) {
		if(event instanceof ISourceChange)
		{
			logger.info("get event.");
			final ISourceChange change = (ISourceChange) event;
			this.detectorsList.bind(new F<IRefactoringDetector, 
					List<IDetectedRefactoring>>(){
				@Override
				public List<IDetectedRefactoring> f(IRefactoringDetector d) {
					return d.detectRefactoring(change);
				}}).foreach(new Effect<IDetectedRefactoring>(){
					@Override
					public void e(IDetectedRefactoring arg0) {
						bus.post(arg0);
					}});
			logger.info("Handled event.");
		}
		return null;
	}

	@Override
	public Void registerListener(Object listener) {
		this.bus.register(listener);
		return null;
	}
}
