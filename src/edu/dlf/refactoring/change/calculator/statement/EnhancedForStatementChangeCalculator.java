package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.EnhancedForStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnhancedForStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SingleVariableDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;

public class EnhancedForStatementChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final F<ASTNodePair, ISourceChange> statementCal;
	private final F<ASTNodePair, ISourceChange> expressionCal;
	private final F<ASTNodePair, ISourceChange> singleVariableCal;
	
	@Inject
	public EnhancedForStatementChangeCalculator(
			Logger logger, 
			@EnhancedForStatementAnnotation String changeLV,
			@SingleVariableDeclarationAnnotation IASTNodeChangeCalculator singleVariableCal,
			@StatementAnnotation IASTNodeChangeCalculator statementCal,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.singleVariableCal = ASTNodePair.splitPairFunc.andThen(SourceChangeUtils.
			getChangeCalculationFunc(singleVariableCal).tuple());
		this.statementCal =  ASTNodePair.splitPairFunc.andThen(SourceChangeUtils.
			getChangeCalculationFunc(statementCal).tuple());
		this.expressionCal =  ASTNodePair.splitPairFunc.andThen(SourceChangeUtils.
			getChangeCalculationFunc(expressionCal).tuple());
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(singleVariableCal.f(pair.selectByPropertyDescriptor
			(EnhancedForStatement.PARAMETER_PROPERTY)));
		container.addSubChange(expressionCal.f(pair.selectByPropertyDescriptor
			(EnhancedForStatement.EXPRESSION_PROPERTY)));
		container.addSubChange(statementCal.f(pair.selectByPropertyDescriptor
			(EnhancedForStatement.BODY_PROPERTY)));
		return container;
	}

}
