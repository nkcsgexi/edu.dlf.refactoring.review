package edu.dlf.refactoring.ui;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.design.ISourceChange;

public class TreeViewLabelProvider implements ILabelProvider
{
	private final String projectLevel;
	private final String packageLevel;
	private final String cuLevel;

	@Inject
	public TreeViewLabelProvider(
			@JavaProjectAnnotation String projectLevel,
			@SourcePackageAnnotation String packageLevel,
			@CompilationUnitAnnotation String cuLevel)
	{
		this.projectLevel = projectLevel;
		this.packageLevel = packageLevel;
		this.cuLevel = cuLevel;
	}
	
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
		if(element instanceof ISourceChange){
			ISourceChange change = (ISourceChange) element;
			if(change.getSourceChangeLevel().equals(this.cuLevel))
			{
				ASTNode root = change.getNodeBefore();
				String fullName = ASTAnalyzer.getMainTypeName(root);
				String[] names = fullName.split(".");
				return fullName;
			}
		}
		return "";
	}	
}

