package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SwitchStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchCaseStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.data.List;

public class SwitchStatementChangeCalculator implements IASTNodeChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator caseCal;
	private final IASTNodeChangeCalculator expressionCal;


	@Inject
	public SwitchStatementChangeCalculator(
			Logger logger,
			@SwitchStatementAnnotation String changeLV,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCal,
			@SwitchCaseStatementAnnotation IASTNodeChangeCalculator caseCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.caseCal = caseCal;
		this.expressionCal = expressionCal;
	}
	
	private final F<ASTNode, List<ASTNode>> getStatementsFunc = 
		ASTNode2ASTNodeUtils.getStructuralPropertyFunc.flip().f(SwitchStatement.
			STATEMENTS_PROPERTY);
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.
			createSubchangeContainer(pair);
		container.addSubChange(expressionCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(SwitchStatement.EXPRESSION_PROPERTY)));
		return container;
	}

}
