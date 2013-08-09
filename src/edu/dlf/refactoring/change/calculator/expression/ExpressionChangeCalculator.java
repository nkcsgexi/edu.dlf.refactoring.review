package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;

import edu.dlf.refactoring.change.ASTAnnotations.AssignmentAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.ExpressionAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.VariableDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class ExpressionChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator vdCalculator;
	private final IASTNodeChangeCalculator asCalculator;
	private final ChangeBuilder changeBuilder;


	public ExpressionChangeCalculator(
			@ExpressionAnnotation String changeLevel,
			@VariableDeclarationAnnotation IASTNodeChangeCalculator vdCalculator,			
			@AssignmentAnnotation IASTNodeChangeCalculator asCalculator)
	{
		this.asCalculator = asCalculator;
		this.vdCalculator = vdCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		
		Expression expBefore, expAfter;
		expBefore = (Expression) pair.getNodeBefore();
		expAfter = (Expression) pair.getNodeAfter();
		
		if(expBefore.getNodeType() != expAfter.getNodeType())
		{
			return changeBuilder.createUnknownChange(pair);
		}
		
		if(expBefore.getNodeType() == ASTNode.ASSIGNMENT)
		{
			return this.asCalculator.CalculateASTNodeChange(pair);
		}
		
		if(expBefore.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION)
		{
			return this.vdCalculator.CalculateASTNodeChange(pair);
		}
		
		return changeBuilder.createUnknownChange(pair);
	}
	
	


}
