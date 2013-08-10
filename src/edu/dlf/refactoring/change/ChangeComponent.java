package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.design.JavaElementPair;

public class ChangeComponent{
	
	private final IJavaModelChangeCalculator cuCalculator;
	private final IJavaModelChangeCalculator packageCalculator;
	private final IJavaModelChangeCalculator projectCalculator;

	@Inject
	public ChangeComponent(@JavaProjectAnnotation IJavaModelChangeCalculator projectCalculator,
			@SourcePackageAnnotation IJavaModelChangeCalculator packageCalculator, 
			@CompilationUnitAnnotation IJavaModelChangeCalculator cuCalculator)
	{
		this.projectCalculator = projectCalculator;
		this.packageCalculator = packageCalculator;
		this.cuCalculator = cuCalculator;
	}
	
	public Void listener(JavaElementPair change)
	{
		if(change.GetBeforeElement() instanceof IJavaProject)
			projectCalculator.CalculateJavaModelChange(change);
		if(change.GetBeforeElement() instanceof ICompilationUnit)
			cuCalculator.CalculateJavaModelChange(change);
		return null;
	}

}
