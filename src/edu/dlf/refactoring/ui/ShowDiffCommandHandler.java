package edu.dlf.refactoring.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.checkers.RefactoringCheckerComponent;
import edu.dlf.refactoring.design.ServiceLocator;

public class ShowDiffCommandHandler extends AbstractHandler {

	private DiffPresenter previousPresenter;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		EventBus bus = ServiceLocator.ResolveType(RefactoringCheckerComponent.
				class);
		if(previousPresenter != null)
			bus.unregister(previousPresenter);
		previousPresenter = new DiffPresenter();
		bus.register(previousPresenter);
		return null;
	}
	

}
