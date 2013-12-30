package edu.dlf.refactoring.change;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringDetectionCompAnnotation;
import edu.dlf.refactoring.utils.WorkQueueItem;


public class ChangeComponent implements IFactorComponent{
	
	private final IJavaModelChangeCalculator icuCalculator;
	private final IJavaModelChangeCalculator packageCalculator;
	private final IJavaModelChangeCalculator projectCalculator;
	private final IASTNodeChangeCalculator cuCalculator;
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final EventBus bus;
	private final ExecutorService queue;
	
	@Inject
	public ChangeComponent(
			ExecutorService queue,
			@JavaProjectAnnotation IJavaModelChangeCalculator projectCalculator,
			@SourcePackageAnnotation IJavaModelChangeCalculator packageCalculator, 
			@CompilationUnitAnnotation IJavaModelChangeCalculator icuCalculator,
			@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculator,
			@RefactoringDetectionCompAnnotation IFactorComponent component)
	{
		this.queue = queue;
		this.projectCalculator = projectCalculator;
		this.packageCalculator = packageCalculator;
		this.icuCalculator = icuCalculator;
		this.cuCalculator = cuCalculator;
		this.bus = new EventBus();
		this.bus.register(component);
	}

	@Subscribe
	@Override
	public Void listen(final Object event) {
		queue.execute(new WorkQueueItem("Change"){
			@Override
			public void internalRun() {
				if(event instanceof JavaElementPair){
					logger.debug("Get event.");
					JavaElementPair change = (JavaElementPair) event;
					if(change.getElementBefore() instanceof IJavaProject)
						postEvent(SourceChangeUtils.pruneSourceChange(
							projectCalculator.CalculateJavaModelChange(change)));
					if(change.getElementBefore() instanceof ICompilationUnit)
						postEvent(SourceChangeUtils.pruneSourceChange(
							icuCalculator.CalculateJavaModelChange(change)));
					logger.debug("Handled event.");
				}
				
				if(event instanceof IASTNodePair) {
					logger.info("Get event.");
					ISourceChange change = SourceChangeUtils.
						pruneSourceChange(cuCalculator.CalculateASTNodeChange
							((ASTNodePair) event));
					postEvent(change);
					logger.info("Handled event.");
				}}});
		return null;
	}
	
	private void postEvent(ISourceChange change) {
		if(change != null)
			bus.post(change);
	}

	@Override
	public Void registerListener(ICompListener listener) {
		bus.register(listener);
		return null;
	}
	
}
