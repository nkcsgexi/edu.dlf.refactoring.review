package edu.dlf.refactoring.detectors;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import edu.dlf.refactoring.utils.XList;
import fj.Effect;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class RefactoringDetectionComponent implements IFactorComponent{
	
	private final List<IRefactoringDetector> detectors;

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

	@Override
	public Void listen(Object event) {
		if(event instanceof ISourceChange)
		{
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
