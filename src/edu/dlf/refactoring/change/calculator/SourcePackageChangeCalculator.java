package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.JavaModelAnnotation.CompilationUnit;
import edu.dlf.refactoring.design.JavaElementPair;

public class SourcePackageChangeCalculator implements IJavaModelChangeCalculator{

	
	private final IJavaModelChangeCalculator _cuChangeCalculator;


	@Inject
	public SourcePackageChangeCalculator(@CompilationUnit IJavaModelChangeCalculator _cuChangeCalculator)
	{
		this._cuChangeCalculator = _cuChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		
		return null;
	}

}
