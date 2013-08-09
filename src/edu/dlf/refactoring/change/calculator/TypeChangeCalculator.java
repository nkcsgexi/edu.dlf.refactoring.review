package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.Method;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class TypeChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator _mChangeCalculator;


	@Inject
	public TypeChangeCalculator(@Method IASTNodeChangeCalculator _mChangeCalculator)
	{
		this._mChangeCalculator = _mChangeCalculator;
	}


	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		// TODO Auto-generated method stub
		return null;
	}

}
