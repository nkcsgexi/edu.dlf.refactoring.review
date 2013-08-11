package edu.dlf.refactoring.change.calculator.statement;

import org.eclipse.jdt.core.dom.ForStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ForStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class ForStatementChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	
	@Inject
	public ForStatementChangeCalculator(
			@ForStatementAnnotation String changeLevel,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator,
			@BlockAnnotation IASTNodeChangeCalculator bCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ASTNodePair bPair = pair.selectByPropertyDescriptor(ForStatement.BODY_PROPERTY);
		ASTNodePair exPair = pair.selectByPropertyDescriptor(ForStatement.EXPRESSION_PROPERTY);
		
		
		
		
		return null;
	}

}
