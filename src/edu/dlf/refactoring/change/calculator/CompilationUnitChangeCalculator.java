package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.design.JavaElementPair;

public class CompilationUnitChangeCalculator implements IJavaModelChangeCalculator{

	private IASTNodeChangeCalculator _typeChangeCalculator;

	@Inject
	public CompilationUnitChangeCalculator(@TypeDeclarationAnnotation IASTNodeChangeCalculator _typeChangeCalculator)
	{
		this._typeChangeCalculator = _typeChangeCalculator;
	}
	
	
	@Override
	public Void CalculateSourceChange(JavaElementPair pair) {
		
		
		
		
		return null;
	}

}
