package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.Assignment;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AssignmentAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class AssignmentChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator exCalculator;
	private final ChangeBuilder changeBuilder;

	@Inject
	public AssignmentChangeCalculator(
			@AssignmentAnnotation String level,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator)
	{
		this.exCalculator = exCalculator;
		this.changeBuilder = new ChangeBuilder(level);
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer(pair);
		Assignment asBefore = (Assignment) pair.getNodeBefore();
		Assignment asAfter = (Assignment) pair.getNodeAfter();
		
		ISourceChange leftChange = exCalculator.CalculateASTNodeChange(new ASTNodePair(asBefore.getLeftHandSide(),
			asAfter.getLeftHandSide()));
		ISourceChange rightChange = exCalculator.CalculateASTNodeChange(new ASTNodePair(asBefore.getRightHandSide(),
			asAfter.getRightHandSide()));
		container.addSubChange(leftChange);
		container.addSubChange(rightChange);		
		return container;
	}
}
