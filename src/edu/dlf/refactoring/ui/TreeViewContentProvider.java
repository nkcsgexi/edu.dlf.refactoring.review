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
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;
import fj.F2;
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
	
	private F2<String, ISourceChange, List<ISourceChange>> getLevelChildren = 
		new F2<String, ISourceChange, List<ISourceChange>>() {
			@Override
			public List<ISourceChange> f(final String level, final ISourceChange parent) {
				return SourceChangeUtils.getChildren(parent).filter(
					new F<ISourceChange, Boolean>() {
					@Override
					public Boolean f(ISourceChange change) {
						return change.getSourceChangeLevel().equals(level) && 
							change.getSourceChangeType() == SourceChangeType.PARENT;
	}});}}; 
	

	@Override
	public Object[] getChildren(Object parentElement) {
		final ISourceChange change = (ISourceChange) parentElement;
		if(change.getSourceChangeLevel().equals(this.cuLevel))
		{
			return null;
		}
		if(change.getSourceChangeLevel().equals(this.projectLevel))
		{
			return getLevelChildren.f(packageLevel, change).filter(
				new F<ISourceChange, Boolean>() {
				@Override
				public Boolean f(ISourceChange change) {
					return getLevelChildren.f(cuLevel, change).isNotEmpty();
				}
			}).toCollection().toArray();
		}
		if(change.getSourceChangeLevel().equals(this.packageLevel))
		{
			return getLevelChildren.f(cuLevel, change).toCollection().toArray();
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
		return getChildren(element) == null ? false : getChildren(element).
			length > 0;
	}
	
}

