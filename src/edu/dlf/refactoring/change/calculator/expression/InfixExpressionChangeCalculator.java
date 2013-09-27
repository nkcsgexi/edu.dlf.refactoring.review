package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.InfixExpression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InfixExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InfixExpressionOperatorAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class InfixExpressionChangeCalculator implements IASTNodeChangeCalculator {

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator exCalculator;
	private final ChangeBuilder operatorChangeBuilder;

	@Inject
	public InfixExpressionChangeCalculator(
			@InfixExpressionOperatorAnnotation String operatorChangeLevel,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator,
			@InfixExpressionAnnotation String changeLevel)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.operatorChangeBuilder = new ChangeBuilder(operatorChangeLevel);
		this.exCalculator = exCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(exCalculator.CalculateASTNodeChange(
			pair.selectByPropertyDescriptor(
				InfixExpression.LEFT_OPERAND_PROPERTY)));
		container.addSubChange(exCalculator.CalculateASTNodeChange(
			pair.selectByPropertyDescriptor(
				InfixExpression.RIGHT_OPERAND_PROPERTY)));
		return container;
	}
}
