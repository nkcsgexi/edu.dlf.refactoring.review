package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.JavaModelAnnotation.CompilationUnit;
import edu.dlf.refactoring.change.JavaModelAnnotation.SourcePackage;
import edu.dlf.refactoring.design.JavaElementPair;

public class ProjectChangeCalculator implements IJavaModelChangeCalculator{
	
	private final IJavaModelChangeCalculator _pChangeCalculator;
	private final IJavaModelChangeCalculator _cuChangeCalculator;

	@Inject
	public ProjectChangeCalculator(@CompilationUnit IJavaModelChangeCalculator _cuChangeCalculator, 
			@SourcePackage IJavaModelChangeCalculator _pChangeCalculator)
	{
		this._cuChangeCalculator = _cuChangeCalculator;
		this._pChangeCalculator = _pChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		
		
		
		return null;
	}
}
