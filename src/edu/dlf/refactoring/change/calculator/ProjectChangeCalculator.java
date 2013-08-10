package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;

public class ProjectChangeCalculator implements IJavaModelChangeCalculator{
	
	private final IJavaModelChangeCalculator _pChangeCalculator;
	private final IJavaModelChangeCalculator _cuChangeCalculator;

	@Inject
	public ProjectChangeCalculator(@CompilationUnitAnnotation IJavaModelChangeCalculator _cuChangeCalculator, 
			@SourcePackageAnnotation IJavaModelChangeCalculator _pChangeCalculator)
	{
		this._cuChangeCalculator = _cuChangeCalculator;
		this._pChangeCalculator = _pChangeCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		
		
		
		return null;
	}
}
