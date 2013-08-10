package edu.dlf.refactoring.change.calculator.expression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class VariableDeclarationFragmentChangeCalculator implements IASTNodeChangeCalculator{

	@Inject
	public VariableDeclarationFragmentChangeCalculator()
	{
		
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		// TODO Auto-generated method stub
		return null;
	}

}
