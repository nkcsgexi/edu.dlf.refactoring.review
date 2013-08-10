package edu.dlf.refactoring.change.calculator.expression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class TypeChangeCalculator implements IASTNodeChangeCalculator {

	@Inject
	public TypeChangeCalculator()
	{
		
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
	
		
		return null;
	}

}
