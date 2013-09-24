package edu.dlf.refactoring.ui;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import edu.dlf.refactoring.utils.WorkQueue;

public class UICompInjector extends AbstractModule{

	@Override
	protected void configure() {
		bind(CodeReviewContext.class).in(Singleton.class);
	}
	
	@Provides
	@Singleton
	private WorkQueue getSingleThreadQueue()
	{
		return new WorkQueue(1);
	}
}
