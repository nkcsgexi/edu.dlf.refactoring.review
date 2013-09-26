package edu.dlf.refactoring.ui;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.WorkQueue;

public class DiffTreeView extends ViewPart {

	private TreeViewer treeViewer;

	public DiffTreeView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		this.treeViewer = new TreeViewer(parent);
		this.treeViewer.isExpandable(true);
		this.treeViewer.setContentProvider((IContentProvider) ServiceLocator.
			ResolveType(ITreeContentProvider.class));
		this.treeViewer.setLabelProvider((IBaseLabelProvider) ServiceLocator.
			ResolveType(ILabelProvider.class));
		this.treeViewer.addDoubleClickListener((IDoubleClickListener) 
			ServiceLocator.ResolveType(IDoubleClickListener.class));
	}
	
	private interface IListener
	{
		@Subscribe
		void listen(Object input);
	}

	public void InputChange(final IJavaElement before, final IJavaElement after)
	{
		WorkQueue queue = ServiceLocator.ResolveType(WorkQueue.class);
		queue.execute(new Runnable(){
			@Override
			public void run() {
				JavaElementPair pair = new JavaElementPair(before, after);
				((IFactorComponent)ServiceLocator.ResolveType(ChangeComponent.class)).
					registerListener(new IListener(){
						@Override
						public void listen(Object input) {
							treeViewer.setInput(input);
							treeViewer.refresh();
						}});
				CodeReviewUIComponent context = ServiceLocator.ResolveType(
					CodeReviewUIComponent.class);
				context.clearContext();
				((IFactorComponent)ServiceLocator.ResolveType(ChangeComponent.class)).
					listen(pair);
			}});
	}
	
	
	@Override
	public void setFocus() {
		
	}
}
