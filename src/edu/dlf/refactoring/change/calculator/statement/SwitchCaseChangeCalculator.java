package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.SwitchCase;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchCaseStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class SwitchCaseChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator expressionCal;

	@Inject
	public SwitchCaseChangeCalculator(
			Logger logger,
			@SwitchCaseStatementAnnotation String changeLV,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.expressionCal = expressionCal;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.
			createSubchangeContainer(pair);
		container.addSubChange(expressionCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(SwitchCase.EXPRESSION_PROPERTY)));
		return container;
	}

}
