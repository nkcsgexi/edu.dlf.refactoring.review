package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.Assignment;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.Expression;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.calculator.NullSourceChange;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class AssignmentChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator exCalculator;


	@Inject
	public AssignmentChangeCalculator(@Expression IASTNodeChangeCalculator exCalculator)
	{
		this.exCalculator = exCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		Assignment asBefore = (Assignment) pair.getNodeBefore();
		Assignment asAfter = (Assignment) pair.getNodeAfter();
		
		ISourceChange leftChange = exCalculator.CalculateASTNodeChange(new ASTNodePair(asBefore.getLeftHandSide(),
			asAfter.getLeftHandSide()));
		ISourceChange rightChange = exCalculator.CalculateASTNodeChange(new ASTNodePair(asBefore.getRightHandSide(),
			asAfter.getRightHandSide()));
		
		if(leftChange instanceof NullSourceChange || rightChange instanceof NullSourceChange)
		{
			if(!(leftChange instanceof NullSourceChange))
			{
				return leftChange;
			} else {
				return rightChange;
			}
		}
		
		return new AssignmentSourceChange(leftChange, rightChange);
	}
	
	
	public class AssignmentSourceChange implements ISourceChange
	{
		private final ISourceChange rightChange;
		private final ISourceChange leftChange;

		public AssignmentSourceChange(ISourceChange leftChange,
				ISourceChange rightChange) {
			this.leftChange = leftChange;
			this.rightChange = rightChange;
		}
	}
}
