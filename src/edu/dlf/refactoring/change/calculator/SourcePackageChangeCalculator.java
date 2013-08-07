package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IChangeCalculator;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.CompilationUnit;
import edu.dlf.refactoring.design.JavaElementPair;

public class SourcePackageChangeCalculator implements IChangeCalculator{

	
	private final IChangeCalculator _cuChangeCalculator;


	@Inject
	public SourcePackageChangeCalculator(@CompilationUnit IChangeCalculator _cuChangeCalculator)
	{
		this._cuChangeCalculator = _cuChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		
		return null;
	}

}
