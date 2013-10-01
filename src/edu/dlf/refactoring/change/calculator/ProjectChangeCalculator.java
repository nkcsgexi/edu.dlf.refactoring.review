package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;

public class ProjectChangeCalculator implements IJavaModelChangeCalculator{
	
	private final IJavaModelChangeCalculator pChangeCalculator;

	@Inject
	public ProjectChangeCalculator(
		@SourcePackageAnnotation IJavaModelChangeCalculator pChangeCalculator)
	{
		this.pChangeCalculator = pChangeCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		
		
		
		return null;
	}
}
