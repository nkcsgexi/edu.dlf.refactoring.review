package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.DoStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.DoStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;

public class DoStatementChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator exCalculator;
	private final IASTNodeChangeCalculator bCalculator;
	private final Logger logger;

	@Inject
	public DoStatementChangeCalculator(
			@DoStatementAnnotation String changeLevel,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator,
			@BlockAnnotation IASTNodeChangeCalculator bCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.exCalculator = exCalculator;
		this.bCalculator = bCalculator;
		this.logger = ServiceLocator.ResolveType(Logger.class);
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		try{
			SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
			container.addSubChange(exCalculator.CalculateASTNodeChange(pair.
					selectByPropertyDescriptor(DoStatement.EXPRESSION_PROPERTY)));
			container.addSubChange(bCalculator.CalculateASTNodeChange(pair.
					selectByPropertyDescriptor(DoStatement.BODY_PROPERTY)));
			return container;
		}catch(Exception e)
		{
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}
}
