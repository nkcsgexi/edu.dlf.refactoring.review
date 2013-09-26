package edu.dlf.refactoring.ui;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.WorkQueue;

public class TreeViewItemDoubleClickListener implements IDoubleClickListener{

	private final String cuLevel;
	private final CodeReviewUIComponent uiComponent;
	private final WorkQueue queue;

	@Inject
	public TreeViewItemDoubleClickListener(
			@CompilationUnitAnnotation String cuLevel,
			CodeReviewUIComponent uiComponent,
			WorkQueue queue)
	{
		this.cuLevel = cuLevel;
		this.uiComponent = uiComponent;
		this.queue = queue;
	}
	
	
	@Override
	public void doubleClick(final DoubleClickEvent event) {	
		queue.execute(new Runnable(){
			@Override
			public void run() {
				ISelection selection = event.getSelection();
				if(!selection.isEmpty())
				{
					ISourceChange change = (ISourceChange) event.getSource();
					if(change.getSourceChangeLevel() == cuLevel)
					{
						uiComponent.updateViewedCode(change.getNodeBefore(), 
							change.getNodeAfter());
					}
				}
		}});
	}
}
