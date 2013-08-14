package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;

public class ChangeComponent implements IFactorComponent{
	
	private final IJavaModelChangeCalculator cuCalculator;
	private final IJavaModelChangeCalculator packageCalculator;
	private final IJavaModelChangeCalculator projectCalculator;

	@Inject
	public ChangeComponent(@JavaProjectAnnotation IJavaModelChangeCalculator projectCalculator,
			@SourcePackageAnnotation IJavaModelChangeCalculator packageCalculator, 
			@CompilationUnitAnnotation IJavaModelChangeCalculator cuCalculator)
	{
		this.projectCalculator = projectCalculator;
		this.packageCalculator = packageCalculator;
		this.cuCalculator = cuCalculator;
	}

	@Override
	public Void listen(Object event) {
		if(event instanceof JavaElementPair){
			EventBus bus = ServiceLocator.ResolveType(EventBus.class);
			JavaElementPair change = (JavaElementPair) event;
			if(change.GetBeforeElement() instanceof IJavaProject)
				bus.post(SourceChangeUtils.pruneSourceChange(
					projectCalculator.CalculateJavaModelChange(change)));
			if(change.GetBeforeElement() instanceof ICompilationUnit)
				bus.post(SourceChangeUtils.pruneSourceChange(
					cuCalculator.CalculateJavaModelChange(change)));		
		}
		return null;
	}

}
