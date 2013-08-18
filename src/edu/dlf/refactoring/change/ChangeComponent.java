package edu.dlf.refactoring.change;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringDetectionCompAnnotation;

public class ChangeComponent implements IFactorComponent{
	
	private final IJavaModelChangeCalculator icuCalculator;
	private final IJavaModelChangeCalculator packageCalculator;
	private final IJavaModelChangeCalculator projectCalculator;
	private final IASTNodeChangeCalculator cuCalculator;
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final EventBus bus;
	
	@Inject
	public ChangeComponent(@JavaProjectAnnotation IJavaModelChangeCalculator projectCalculator,
			@SourcePackageAnnotation IJavaModelChangeCalculator packageCalculator, 
			@CompilationUnitAnnotation IJavaModelChangeCalculator icuCalculator,
			@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculator,
			@RefactoringDetectionCompAnnotation IFactorComponent component)
	{
		this.projectCalculator = projectCalculator;
		this.packageCalculator = packageCalculator;
		this.icuCalculator = icuCalculator;
		this.cuCalculator = cuCalculator;
		this.bus = new EventBus();
		this.bus.register(component);
	}

	@Subscribe
	@Override
	public Void listen(Object event) {
		if(event instanceof JavaElementPair){
			logger.info("Get event.");
			JavaElementPair change = (JavaElementPair) event;
			if(change.GetBeforeElement() instanceof IJavaProject)
				bus.post(SourceChangeUtils.pruneSourceChange(
					projectCalculator.CalculateJavaModelChange(change)));
			if(change.GetBeforeElement() instanceof ICompilationUnit)
				bus.post(SourceChangeUtils.pruneSourceChange(
					icuCalculator.CalculateJavaModelChange(change)));		
		}
		
		if(event instanceof IASTNodePair)
		{
			logger.info("Get event.");
			bus.post(SourceChangeUtils.pruneSourceChange(cuCalculator.
				CalculateASTNodeChange((ASTNodePair) event)));
		}
		
		return null;
	}

}
