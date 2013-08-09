package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IfStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ASTAnnotations.Expression;
import edu.dlf.refactoring.change.ASTAnnotations.Statement;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class IfStatementChangeCalculator implements IASTNodeChangeCalculator {

	
	private final IASTNodeChangeCalculator statementChangeCalculator;
	private final IASTNodeChangeCalculator expressionChangeCalculator;
	
	@Inject
	public IfStatementChangeCalculator(@Statement IASTNodeChangeCalculator statementChangeCalculator,
			@Expression IASTNodeChangeCalculator expressionChangeCalculator)
	{
		this.statementChangeCalculator = statementChangeCalculator;
		this.expressionChangeCalculator = expressionChangeCalculator;
		
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		IfStatement ifBefore = (IfStatement)pair.getNodeBefore();
		IfStatement ifAfter = (IfStatement)pair.getNodeAfter();
		
		ISourceChange eChange = new NullSourceChange();
		ISourceChange thenChange = new NullSourceChange();
		ISourceChange elseChange = new NullSourceChange();
		if(!ASTAnalyzer.areASTNodesSame(ifBefore.getExpression(), ifAfter.getExpression()))
		{
			eChange = expressionChangeCalculator.CalculateASTNodeChange(new ASTNodePair(ifBefore.getExpression(), 
					ifAfter.getExpression()));
		}
		
		if(!ASTAnalyzer.areASTNodesSame(getThenStatement(ifBefore), getThenStatement(ifAfter)))
		{
			thenChange = statementChangeCalculator.CalculateASTNodeChange(new ASTNodePair(ifBefore.getThenStatement(), 
					ifAfter.getThenStatement()));
		}
		
		if(!ASTAnalyzer.areASTNodesSame(getElseStatement(ifBefore), getElseStatement(ifAfter)))
		{
			elseChange = statementChangeCalculator.CalculateASTNodeChange(new ASTNodePair(ifBefore.getElseStatement(),
					ifAfter.getElseStatement()));
		}
		return new IfStatementChange(eChange, thenChange, elseChange);
	}
	
	
	private ASTNode getThenStatement(IfStatement statement)
	{
		return (ASTNode) statement.getStructuralProperty(IfStatement.THEN_STATEMENT_PROPERTY); 
	}
	
	private ASTNode getElseStatement(IfStatement statement)
	{
		return (ASTNode) statement.getStructuralProperty(IfStatement.ELSE_STATEMENT_PROPERTY);
	}
	
	
	public class IfStatementChange implements ISourceChange
	{
		private final ISourceChange eChange;
		private final ISourceChange tChange;
		private final ISourceChange elseChange;

		protected IfStatementChange(ISourceChange eChange, ISourceChange tChange, ISourceChange elseChange)
		{
			this.eChange = eChange;
			this.tChange = tChange;
			this.elseChange = elseChange;
		}
		
		public ISourceChange getExpressionChange()
		{
			return this.eChange;
		}
		
		public ISourceChange getThenChange()
		{
			return this.tChange;
		}
		
		public ISourceChange getElseChange()
		{
			return this.elseChange;
		}
	}
	

}
