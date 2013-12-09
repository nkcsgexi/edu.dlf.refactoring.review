package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.InstanceofExpression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InstanceOfExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class InstanceOfChangeCalculator implements IASTNodeChangeCalculator{
	
	private final Logger logger;
	private final IASTNodeChangeCalculator typeCal;
	private final IASTNodeChangeCalculator expressionCal;
	private final ChangeBuilder changeBuilder;

	@Inject
	public InstanceOfChangeCalculator(Logger logger,
			@InstanceOfExpressionAnnotation String changeLV,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCal,
			@TypeAnnotation IASTNodeChangeCalculator typeCal) {
		this.logger = logger;
		this.expressionCal = expressionCal;
		this.typeCal = typeCal;
		this.changeBuilder = new ChangeBuilder(changeLV);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer
			(pair);
		container.addSubChange(expressionCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(InstanceofExpression.LEFT_OPERAND_PROPERTY)));
		container.addSubChange(typeCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(InstanceofExpression.RIGHT_OPERAND_PROPERTY)));
		return container;
	}

}
