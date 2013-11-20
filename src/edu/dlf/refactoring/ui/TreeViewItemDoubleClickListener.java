package edu.dlf.refactoring.ui;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator.UICompAnnotation;
import edu.dlf.refactoring.utils.WorkQueue;
import edu.dlf.refactoring.utils.WorkQueueItem;

public class TreeViewItemDoubleClickListener implements IDoubleClickListener{

	private final String cuLevel;
	private final CodeReviewUIComponent uiComponent;
	private final WorkQueue queue;

	@Inject
	public TreeViewItemDoubleClickListener(
			@CompilationUnitAnnotation String cuLevel,
			@UICompAnnotation IFactorComponent uiComponent,
			WorkQueue queue)
	{
		this.cuLevel = cuLevel;
		this.uiComponent = (CodeReviewUIComponent) uiComponent;
		this.queue = queue;
	}
	
	
	@Override
	public void doubleClick(final DoubleClickEvent event) {	
		queue.execute(new WorkQueueItem("DoubleClick"){
			@Override
			public void internalRun() {
				ISelection selection = event.getSelection();
				if(!selection.isEmpty())
				{
					TreeViewer viewer = (TreeViewer) event.getViewer();
				    IStructuredSelection thisSelection = (IStructuredSelection) 
				    		event.getSelection(); 
				    Object element = thisSelection.getFirstElement(); 
					
					ISourceChange change = (ISourceChange) element;
					if(change.getSourceChangeLevel() == cuLevel)
					{
						uiComponent.updateViewedCode(change.getNodeBefore(), 
							change.getNodeAfter());
					}
				}
		}});
	}
}
