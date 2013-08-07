package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IChangeCalculator;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.Type;
import edu.dlf.refactoring.design.JavaElementPair;

public class CompilationUnitChangeCalculator implements IChangeCalculator{

	private IChangeCalculator _typeChangeCalculator;

	@Inject
	public CompilationUnitChangeCalculator(@Type IChangeCalculator _typeChangeCalculator)
	{
		this._typeChangeCalculator = _typeChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		
		
		
		
		return null;
	}

}
