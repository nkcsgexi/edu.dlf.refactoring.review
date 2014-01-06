package edu.dlf.refactoring.detectors;

import static fj.data.List.list;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangedLinesComputer;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringImplementaterCompAnnotation;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractSuperType;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.InlineMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameField;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameLocalVariable;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import edu.dlf.refactoring.study.StudyUtils;
import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.List;

public class RefactoringDetectionComponent implements IFactorComponent{
	
	private final Logger logger;
	private final List<IRefactoringDetector> detectorsList;
	private final EventBus bus;
	private final ChangedLinesComputer lineComputer;

	@Inject
	public RefactoringDetectionComponent(
			Logger logger,
			ChangedLinesComputer lineComputer,
			@RenameMethod IRefactoringDetector rmDetector,
			@ExtractMethod IRefactoringDetector emDetector,
			@InlineMethod IRefactoringDetector inlineDetector,
			@ExtractSuperType IRefactoringDetector extractSuperDet,
			@RenameType IRefactoringDetector rtDetector,
			@RenameLocalVariable IRefactoringDetector rlvDetector,
			@RenameField IRefactoringDetector rfDetector,
			@MoveResource IRefactoringDetector mDetector,
			@RefactoringImplementaterCompAnnotation IFactorComponent component) {
		this.logger = logger;
		this.detectorsList = list(rmDetector, emDetector, inlineDetector, 
			rfDetector, mDetector, rtDetector, rlvDetector, extractSuperDet);
		this.bus = new EventBus();
		this.lineComputer = lineComputer;
		bus.register(component);
	}
	
	@Subscribe
	@Override
	public Void listen(final Object event) {
		if(event instanceof ISourceChange) {
			final ISourceChange change = (ISourceChange) event;
			logger.info(SourceChangeUtils.printChangeTree(change));
			StudyUtils.logRevisionStart();
			StudyUtils.logChangedLines.e(change);
			detectorsList.bind(new F<IRefactoringDetector, List<
					IDetectedRefactoring>>(){
				@Override
				public List<IDetectedRefactoring> f(IRefactoringDetector d) {
					return d.f(change);
				}}).foreach(new Effect<IDetectedRefactoring>(){
					@Override
					public void e(IDetectedRefactoring refactoring) {
						StudyUtils.logDetectedRefactoring.e(refactoring);
						bus.post(refactoring);
					}});}
		return null;
	}

	@Override
	public Void registerListener(ICompListener listener) {
		this.bus.register(listener);
		return null;
	}
}
