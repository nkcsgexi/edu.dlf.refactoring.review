package edu.dlf.refactoring.detectors;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import edu.dlf.refactoring.utils.XList;

public class RefactoringDetectionComponent implements IFactorComponent{
	
	XList<IRefactoringDetector> detectors = new XList<IRefactoringDetector>();
	
	public RefactoringDetectionComponent(
			@RenameMethod IRefactoringDetector rmDetector,
			@ExtractMethod IRefactoringDetector emDetector,
			@RenameType IRefactoringDetector rtDetector)
	{
		detectors.add(rmDetector);
		detectors.add(emDetector);
		detectors.add(rtDetector);
	}

	@Override
	public Void listen(Object event) {
		if(event instanceof ISourceChange)
		{
			final ISourceChange change = (ISourceChange) event;
			detectors.selectMany(new Function<IRefactoringDetector, 
					Collection<IRefactoring>>(){
				@Override
				public Collection<IRefactoring> apply(IRefactoringDetector d) {
					return d.detectRefactoring(change);
				}}).operateOnElement(new Function<IRefactoring, Void>(){
					@Override
					public Void apply(IRefactoring ref) {
						EventBus bus = ServiceLocator.ResolveType(EventBus.class);
						bus.post(ref);
						return null;
					}});;
		}
		return null;
	}
}
