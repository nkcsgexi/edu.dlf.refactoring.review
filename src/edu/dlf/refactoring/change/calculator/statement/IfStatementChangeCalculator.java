package edu.dlf.refactoring.change.calculator.statement;

import org.eclipse.jdt.core.dom.IfStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.IfStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class IfStatementChangeCalculator implements IASTNodeChangeCalculator {

	
	private final IASTNodeChangeCalculator statementChangeCalculator;
	private final IASTNodeChangeCalculator expressionChangeCalculator;
	private final ChangeBuilder changeBuilder;
	
	@Inject
	public IfStatementChangeCalculator(
			@IfStatementAnnotation String changeLevel,
			@StatementAnnotation IASTNodeChangeCalculator statementChangeCalculator,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionChangeCalculator)
	{
		this.statementChangeCalculator = statementChangeCalculator;
		this.expressionChangeCalculator = expressionChangeCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
		
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		IfStatement ifBefore = (IfStatement)pair.getNodeBefore();
		IfStatement ifAfter = (IfStatement)pair.getNodeAfter();
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);	
		container.addSubChange(expressionChangeCalculator.CalculateASTNodeChange(new 
				ASTNodePair(ifBefore.getExpression(), ifAfter.getExpression())));
		container.addSubChange(statementChangeCalculator.CalculateASTNodeChange(new 
				ASTNodePair(ifBefore.getThenStatement(), ifAfter.getThenStatement())));
		container.addSubChange(statementChangeCalculator.CalculateASTNodeChange(new 
				ASTNodePair(ifBefore.getElseStatement(), ifAfter.getElseStatement())));
		return container;
	}
}
