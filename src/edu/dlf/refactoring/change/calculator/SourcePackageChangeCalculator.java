package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;

public class SourcePackageChangeCalculator implements IJavaModelChangeCalculator{

	
	private final IJavaModelChangeCalculator _cuChangeCalculator;


	@Inject
	public SourcePackageChangeCalculator(@CompilationUnitAnnotation IJavaModelChangeCalculator _cuChangeCalculator)
	{
		this._cuChangeCalculator = _cuChangeCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		
		return null;
	}

}
