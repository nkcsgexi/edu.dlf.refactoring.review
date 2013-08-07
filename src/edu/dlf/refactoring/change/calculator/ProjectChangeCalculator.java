package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IChangeCalculator;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.CompilationUnit;
import edu.dlf.refactoring.change.JavaModelLevelAnnotation.SourcePackage;
import edu.dlf.refactoring.design.JavaElementPair;

public class ProjectChangeCalculator implements IChangeCalculator{
	
	private final IChangeCalculator _pChangeCalculator;
	private final IChangeCalculator _cuChangeCalculator;

	@Inject
	public ProjectChangeCalculator(@CompilationUnit IChangeCalculator _cuChangeCalculator, 
			@SourcePackage IChangeCalculator _pChangeCalculator)
	{
		this._cuChangeCalculator = _cuChangeCalculator;
		this._pChangeCalculator = _pChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		
		
		
		return null;
	}
}
