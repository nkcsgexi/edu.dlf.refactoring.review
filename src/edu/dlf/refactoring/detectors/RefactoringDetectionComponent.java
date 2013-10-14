package edu.dlf.refactoring.detectors;

import static fj.data.List.list;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringCheckerCompAnnotation;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import edu.dlf.refactoring.utils.WorkQueue;
import fj.Effect;
import fj.F;
import fj.data.List;

public class RefactoringDetectionComponent implements IFactorComponent{
	
	private final Logger logger;
	private final List<IRefactoringDetector> detectorsList;
	private final EventBus bus;
	private final WorkQueue queue;

	@Inject
	public RefactoringDetectionComponent(
			WorkQueue queue,
			Logger logger,
			@RenameMethod IRefactoringDetector rmDetector,
			@ExtractMethod IRefactoringDetector emDetector,
			@RenameType IRefactoringDetector rtDetector,
			@MoveResource IRefactoringDetector mDetector,
			@RefactoringCheckerCompAnnotation IFactorComponent component)
	{
		this.queue = queue;
		this.logger = logger;
		this.detectorsList = list(rmDetector, emDetector, rtDetector, mDetector);
		this.bus = new EventBus();
		bus.register(component);
	}
	
	@Subscribe
	@Override
	public Void listen(final Object event) {
		queue.execute(new Runnable(){
			@Override
			public void run() {
				if(event instanceof ISourceChange)
				{
					logger.info("get event.");
					final ISourceChange change = (ISourceChange) event;
					detectorsList.bind(new F<IRefactoringDetector, 
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
			}});
		return null;
	}

	@Override
	public Void registerListener(ICompListener listener) {
		this.bus.register(listener);
		return null;
	}
}
