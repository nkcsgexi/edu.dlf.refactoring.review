package edu.dlf.refactoring.ui;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.UIUtils;

public class TreeViewLabelProvider implements ILabelProvider
{
	private final HashMap<String, Image> map;
	private final Logger logger;

	@Inject
	public TreeViewLabelProvider(
			Logger logger,
			@JavaProjectAnnotation String projectLevel,
			@SourcePackageAnnotation String packageLevel,
			@CompilationUnitAnnotation String cuLevel) {
		this.logger = logger;
		this.map = new HashMap<String, Image>();
		this.map.put(cuLevel, UIUtils.createImage("file.gif"));
		this.map.put(packageLevel, UIUtils.createImage("package.gif"));
		this.map.put(projectLevel, UIUtils.createImage("project.gif"));
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
		return this.map.get(((ISourceChange)element).getSourceChangeLevel());
	}

	@Override
	public String getText(Object element) {
		if(element instanceof ISourceChange){
			ISourceChange change = (ISourceChange) element;
			if(change.getElementBefore() != null){
				String before = change.getElementBefore().getElementName();
				String after = change.getElementAfter().getElementName();
				return before + "=>" + after;
			}else
			{
				String before = change.getNodeBefore() == null ? "" : ASTAnalyzer.
					getJavaElement(change.getNodeBefore()).getElementName();
				String after = change.getNodeAfter() == null ? "" : ASTAnalyzer.
					getJavaElement(change.getNodeAfter()).getElementName();
				return before + "=>" + after;
			}
		}
		return "";
	}	
}

