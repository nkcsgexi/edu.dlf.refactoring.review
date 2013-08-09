package edu.dlf.refactoring.change.calculator;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.IfStatement;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class BlockChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator ifStatementCalculator;


	@Inject
	public BlockChangeCalculator(@IfStatement IASTNodeChangeCalculator ifStatementCalculator)
	{
		this.ifStatementCalculator = ifStatementCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
	
		
		return null;
	}

}
