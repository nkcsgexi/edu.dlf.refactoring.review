package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.Block;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;

public class IfStatementChangeCalculator implements IASTNodeChangeCalculator {

	
	private final IASTNodeChangeCalculator blockChangeCalculator;
	
	@Inject
	public IfStatementChangeCalculator(@Block IASTNodeChangeCalculator blockChangeCalculator)
	{
		this.blockChangeCalculator = blockChangeCalculator;
	}
	
	@Override
	public Void CalculateASTNodeChange(ASTNodePair pair) {
		
		
		
		return null;
	}

}
