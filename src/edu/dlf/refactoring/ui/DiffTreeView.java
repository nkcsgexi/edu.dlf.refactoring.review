package edu.dlf.refactoring.ui;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class DiffTreeView extends ViewPart {

	private TreeViewer treeViewer;

	public DiffTreeView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		this.treeViewer = new TreeViewer(parent);
		this.treeViewer.isExpandable(true);
		this.treeViewer.setContentProvider(new ContentProvider());
		this.treeViewer.setLabelProvider(new LabelProvider());
		this.treeViewer.addDoubleClickListener(new DoubleClickListener());
		this.treeViewer.setInput(new Object());
	}
	

	@Override
	public void setFocus() {
		
	}
	
	private class ContentProvider implements ITreeContentProvider
	{
		@Override
		public void dispose() {
				
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return new String[] {"a"};
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}
	}
	
	
	private class LabelProvider implements ILabelProvider
	{
		@Override
		public void addListener(ILabelProviderListener listener) {

		}

		@Override
		public void dispose() {
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			
		}

		@Override
		public Image getImage(Object element) {
			return null;
		}

		@Override
		public String getText(Object element) {
			return "Item";
		}	
	}
	
	private class DoubleClickListener implements IDoubleClickListener
	{
		@Override
		public void doubleClick(DoubleClickEvent event) {
		
	

		}	
	}
	

}
