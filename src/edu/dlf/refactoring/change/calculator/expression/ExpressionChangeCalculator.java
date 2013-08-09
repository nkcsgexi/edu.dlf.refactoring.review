package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;

import edu.dlf.refactoring.change.ASTAnnotations.Assignment;
import edu.dlf.refactoring.change.ASTAnnotations.VariableDeclaration;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.calculator.NullSourceChange;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class ExpressionChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator vdCalculator;
	private final IASTNodeChangeCalculator asCalculator;


	public ExpressionChangeCalculator(
			@VariableDeclaration IASTNodeChangeCalculator vdCalculator,			
			@Assignment IASTNodeChangeCalculator asCalculator)
	{
		this.asCalculator = asCalculator;
		this.vdCalculator = vdCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		Expression expBefore, expAfter;
		expBefore = (Expression) pair.getNodeBefore();
		expAfter = (Expression) pair.getNodeAfter();
		
		if(expBefore.getNodeType() != expAfter.getNodeType())
		{
			return new ExpressionTypeChange(expBefore, expAfter);
		}
		
		if(expBefore.getNodeType() == ASTNode.ASSIGNMENT)
		{
			return this.asCalculator.CalculateASTNodeChange(pair);
		}
		
		if(expBefore.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION)
		{
			return this.vdCalculator.CalculateASTNodeChange(pair);
		}
		
		return new NullSourceChange();
	}
	
	
	public class ExpressionTypeChange implements ISourceChange
	{
		private final ASTNode after;
		private final ASTNode before;

		protected ExpressionTypeChange(ASTNode before, ASTNode after)
		{
			this.before = before;
			this.after = after;
		}
		
		public ASTNode getExpressionBefore()
		{
			return this.before;
		}
		
		public ASTNode getExpressionAfter()
		{
			return this.after;
		}
	}

}
