package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IChangeCalculator;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.Method;
import edu.dlf.refactoring.design.JavaElementPair;

public class TypeChangeCalculator implements IChangeCalculator{

	private final IChangeCalculator _mChangeCalculator;


	@Inject
	public TypeChangeCalculator(@Method IChangeCalculator _mChangeCalculator)
	{
		this._mChangeCalculator = _mChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		// TODO Auto-generated method stub
		return null;
	}

}
