package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import edu.dlf.refactoring.change.ChangedLinesComputer;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ComponentsRepository;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.data.List;

public class DiffTreeView extends ViewPart implements ICompListener{

	private TreeViewer treeViewer;
	private final Logger logger;
	private final ChangedLinesComputer computer;

	private final Effect<ISourceChange> computerChangedLines = 
		new Effect<ISourceChange>() {
		@Override
		public void e(ISourceChange change) {
			computer.startCompute((ISourceChange)change);
    		logger.debug("Changed lines: " + computer.getChangedLines());
    		logger.debug("Added lines: " + computer.getAddedLines());
    		logger.debug("Removed lines: " + computer.getRemovedLines());
	}}; 
	
	public DiffTreeView() {
		logger = ServiceLocator.ResolveType(Logger.class);
		((ComponentsRepository)ServiceLocator.ResolveType(ComponentsRepository.
			class)).getChangeComponent().registerListener(this);
		this.computer = ServiceLocator.ResolveType(ChangedLinesComputer.class);
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

	@Override
	public void setFocus() {
		
	}

	@Override
	public void callBack(final Object change) {
		logger.info("Get change.");
		computerChangedLines.e((ISourceChange)change);
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	if(change instanceof ISourceChange){
		    		logger.debug(SourceChangeUtils.printChangeTree(
		    			(ISourceChange)change));
		    		treeViewer.setInput(List.single(change));
		    		treeViewer.refresh();
		}}});
	}
}
