package edu.dlf.refactoring.change.calculator.expression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.LiteralAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class LiteralChangeCalculator implements IASTNodeChangeCalculator{

	
	private final ChangeBuilder changeBuilder;

	@Inject
	public LiteralChangeCalculator(@LiteralAnnotation String changeLevel)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);		
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		return changeBuilder.createUpdateNodesChange(pair);
	}
}
