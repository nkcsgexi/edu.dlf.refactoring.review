package edu.dlf.refactoring.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.checkers.RefactoringCheckerComponent;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.WorkQueue;

public class RefReviewCommandHandler extends AbstractHandler {

	private final WorkQueue queue = ServiceLocator.ResolveType(WorkQueue.class);
	private CodeReviewContext currentPresenter;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getCommand().getId();
		
		if(id.equals("RefReview.CalculateDiff")){
			queue.execute(new Runnable(){
				@Override
				public void run() {
					EventBus bus = ServiceLocator.ResolveType(
						RefactoringCheckerComponent.class);
					if(currentPresenter != null)
						bus.unregister(currentPresenter);
					currentPresenter = new CodeReviewContext();
					bus.register(currentPresenter);
				}});
		}
		
		if(id.equals("RefReview.ChangeViewedCode"))
		{
			queue.execute(new Runnable(){
				@Override
				public void run() {
					
					
				}});
		}
		
		return null;
	}

	private String getSourceAfter() {
		
		return null;
	}

	private String getSourceBefore() {
		
		return null;
	}
	

}
