package edu.dlf.refactoring.ui;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import edu.dlf.refactoring.utils.WorkQueue;

public class UICompInjector extends AbstractModule{

	@Override
	protected void configure() {
		bind(ILabelProvider.class).to(TreeViewLabelProvider.class).in(Singleton.class);
		bind(ITreeContentProvider.class).to(TreeViewContentProvider.class).in(Singleton.class);
		bind(IDoubleClickListener.class).to(TreeViewItemDoubleClickListener.class).in(Singleton.class);
		bind(ICodeReviewInput.class).to(FakeCodeReviewInput.class).in(Singleton.class);
	}
	
	@Provides
	@Singleton
	private WorkQueue getSingleThreadQueue()
	{
		return new WorkQueue(1);
	}
}
