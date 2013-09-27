package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.WhileStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.WhileStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;

public class WhileStatementChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator exCalculator;
	private final IASTNodeChangeCalculator bCalculator;
	private final Logger logger;

	@Inject
	public WhileStatementChangeCalculator(
			@WhileStatementAnnotation String changeLevel, 
			@BlockAnnotation IASTNodeChangeCalculator bCalculator,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.bCalculator = bCalculator;
		this.exCalculator = exCalculator;
		this.logger = ServiceLocator.ResolveType(Logger.class);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		try {
			SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
			container.addSubChange(exCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor(
				WhileStatement.EXPRESSION_PROPERTY)));
			container.addSubChange(bCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor(
				WhileStatement.BODY_PROPERTY)));
			return container;
		} catch (Exception e) {
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}

}
