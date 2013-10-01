package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;
import fj.data.List;
import fj.data.Option;


public class TreeViewContentProvider implements ITreeContentProvider
{
	private final Logger logger;
	private final String projectLevel;
	private final String packageLevel;
	private final String cuLevel;
	
	@Inject
	public TreeViewContentProvider(
			@JavaProjectAnnotation String projectLevel,
			@SourcePackageAnnotation String packageLevel,
			@CompilationUnitAnnotation String cuLevel)
	{
		this.logger = ServiceLocator.ResolveType(Logger.class);
		this.projectLevel = projectLevel;
		this.packageLevel = packageLevel;
		this.cuLevel = cuLevel;
	}
	
	@Override
	public void dispose() {
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return new Object[] {((List)inputElement).head()};
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		final ISourceChange change = (ISourceChange) parentElement;
		if(change.getSourceChangeLevel().equals(this.cuLevel))
		{
			return null;
		}
		List<ISourceChange> subChanges = SourceChangeUtils.getChildren(change);
		if(change.getSourceChangeLevel().equals(this.projectLevel))
		{
			return subChanges.filter(new F<ISourceChange, Boolean>(){
				@Override
				public Boolean f(ISourceChange c) {
					return c.getSourceChangeLevel().equals(packageLevel);
				}}).toCollection().toArray();
		}
		if(change.getSourceChangeLevel().equals(this.packageLevel))
		{
			return subChanges.filter(new F<ISourceChange, Boolean>(){
				@Override
				public Boolean f(ISourceChange c) {
					return c.getSourceChangeLevel().equals(cuLevel);
				}}).toCollection().toArray();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		ISourceChange change = (ISourceChange) element;
		List<ISourceChange> ancestors = SourceChangeUtils.getAncestors(change);
		Option<ISourceChange> parent = ancestors.find(new F<ISourceChange, Boolean>(){
			@Override
			public Boolean f(ISourceChange c) {
				return c.getSourceChangeLevel().equals(projectLevel) ||
						c.getSourceChangeLevel().equals(packageLevel);
			}});
		if(parent.isSome())
			return parent.some();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return !((ISourceChange)element).getSourceChangeLevel().equals
			(this.cuLevel);
	}
	
}

