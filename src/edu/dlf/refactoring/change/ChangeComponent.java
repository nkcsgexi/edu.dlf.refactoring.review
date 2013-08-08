package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import com.google.inject.Inject;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.CompilationUnit;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.JavaProject;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.SourcePackage;
import edu.dlf.refactoring.design.JavaElementPair;

public class ChangeComponent{
	
	private final IJavaModelChangeCalculator cuCalculator;
	private final IJavaModelChangeCalculator packageCalculator;
	private final IJavaModelChangeCalculator projectCalculator;

	@Inject
	public ChangeComponent(@JavaProject IJavaModelChangeCalculator projectCalculator,
			@SourcePackage IJavaModelChangeCalculator packageCalculator, 
			@CompilationUnit IJavaModelChangeCalculator cuCalculator)
	{
		this.projectCalculator = projectCalculator;
		this.packageCalculator = packageCalculator;
		this.cuCalculator = cuCalculator;
	}
	
	public Void listener(JavaElementPair change)
	{
		if(change.GetBeforeElement() instanceof IJavaProject)
			projectCalculator.CalculateSourceChange(change);
		if(change.GetBeforeElement() instanceof ICompilationUnit)
			cuCalculator.CalculateSourceChange(change);
		return null;
	}

}
