package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ExpressionStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.Block;
import edu.dlf.refactoring.change.ASTAnnotations.Expression;
import edu.dlf.refactoring.change.ASTAnnotations.IfStatement;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class StatementChangeCalculator implements IASTNodeChangeCalculator {

	private final IASTNodeChangeCalculator blockCalculator;
	private final IASTNodeChangeCalculator ifCalculator;
	private final IASTNodeChangeCalculator expressionCalculator;

	@Inject
	public StatementChangeCalculator(
			@IfStatement IASTNodeChangeCalculator ifCalculator,
			@Block IASTNodeChangeCalculator blockCalculator, 
			@Expression IASTNodeChangeCalculator expressionCalculator) {
		this.ifCalculator = ifCalculator;
		this.blockCalculator = blockCalculator;
		this.expressionCalculator = expressionCalculator;
	}

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		if (pair.getNodeBefore() == null || pair.getNodeAfter() == null) {
			if (pair.getNodeBefore() != null) {
				return new RemoveStatementChange(pair.getNodeBefore());
			} else if (pair.getNodeAfter() != null) {
				return new AddStatementChange(pair.getNodeAfter());
			} else
				return new NullSourceChange();
		}
		
		if(pair.getNodeBefore().getNodeType() != pair.getNodeAfter().getNodeType())
			return new UpdateStatementTypeChange(pair.getNodeBefore(), pair.getNodeAfter());
	
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
			return expressionCalculator.CalculateASTNodeChange(new ASTNodePair(
				(ASTNode)pair.getNodeBefore().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY), 
				(ASTNode)pair.getNodeAfter().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY)));
		}

		return new NullSourceChange();
	}

	public class AddStatementChange implements ISourceChange {
		private final ASTNode node;

		protected AddStatementChange(ASTNode node)
		{
			this.node = node;
		}
		
		public ASTNode getAddedStatement()
		{
			return node;
		}
	}

	public class RemoveStatementChange implements ISourceChange {
		private final ASTNode node;

		protected RemoveStatementChange (ASTNode node)
		{
			this.node = node;
		}
		
		public ASTNode getRemovedStatement()
		{
			return node;
		}
	}

	public class UpdateStatementTypeChange implements ISourceChange {
		private final ASTNode before;
		private final ASTNode after;

		protected UpdateStatementTypeChange(ASTNode before, ASTNode after)
		{
			this.before = before;
			this.after = after;
		}
		
		public ASTNode getStatementBefore()
		{
			return before;
		}
		
		public ASTNode getStatementAfter()
		{
			return after;
		}
	}

}
