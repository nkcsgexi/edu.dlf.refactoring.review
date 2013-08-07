package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;

import com.google.inject.Inject;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.CompilationUnit;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.JavaProject;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.SourcePackage;
import edu.dlf.refactoring.design.JavaElementPair;

public class ChangeComponent{
	
	private final IChangeCalculator cuCalculator;
	private final IChangeCalculator packageCalculator;
	private final IChangeCalculator projectCalculator;

	@Inject
	public ChangeComponent(@JavaProject IChangeCalculator projectCalculator,
			@SourcePackage IChangeCalculator packageCalculator, 
			@CompilationUnit IChangeCalculator cuCalculator)
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
