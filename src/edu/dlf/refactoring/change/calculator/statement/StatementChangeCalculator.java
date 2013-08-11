package edu.dlf.refactoring.change.calculator.statement;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ExpressionStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.IfStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class StatementChangeCalculator implements IASTNodeChangeCalculator {

	private final IASTNodeChangeCalculator blockCalculator;
	private final IASTNodeChangeCalculator ifCalculator;
	private final IASTNodeChangeCalculator expressionCalculator;
	private final ChangeBuilder changeBuilder;

	@Inject
	public StatementChangeCalculator(
			@StatementAnnotation String changeLevel,
			@IfStatementAnnotation IASTNodeChangeCalculator ifCalculator,
			@BlockAnnotation IASTNodeChangeCalculator blockCalculator, 
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCalculator) {
		this.ifCalculator = ifCalculator;
		this.blockCalculator = blockCalculator;
		this.expressionCalculator = expressionCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		if(pair.getNodeBefore().getNodeType() != pair.getNodeAfter().getNodeType())
			return this.changeBuilder.createUnknownChange(pair);
	
		if(pair.getNodeBefore().getNodeType() == ASTNode.IF_STATEMENT)
		{
			return ifCalculator.CalculateASTNodeChange(pair);
		}
		
		if(pair.getNodeBefore().getNodeType() == ASTNode.BLOCK)
		{
			return blockCalculator.CalculateASTNodeChange(pair);
		}
		
		if(pair.getNodeBefore().getNodeType() == ASTNode.EXPRESSION_STATEMENT)
		{
			SubChangeContainer container = this.changeBuilder.createSubchangeContainer();
			container.addSubChange(expressionCalculator.CalculateASTNodeChange(new ASTNodePair(
				(ASTNode)pair.getNodeBefore().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY), 
				(ASTNode)pair.getNodeAfter().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY))));
			return container;
		}
		
		return changeBuilder.createUnknownChange(pair);
	}



}
