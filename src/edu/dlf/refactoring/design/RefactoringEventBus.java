package edu.dlf.refactoring.design;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.processors.ExtractMethodProcessor;
import edu.dlf.refactoring.processors.RenameMethodProcessor;
import edu.dlf.refactoring.processors.RenameTypeProcessor;

public class RefactoringEventBus extends EventBus{

	private final EventBus _eventBus = new EventBus();
	
	public RefactoringEventBus()
	{
		super();
		this._eventBus.register(ServiceLocator.ResolveType(RenameMethodProcessor.class));
		this._eventBus.register(ServiceLocator.ResolveType(ExtractMethodProcessor.class));
		this._eventBus.register(ServiceLocator.ResolveType(RenameTypeProcessor.class));
		this._eventBus.register(ServiceLocator.ResolveType(ChangeComponent.class));
	}
}
