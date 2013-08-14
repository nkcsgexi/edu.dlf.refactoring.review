package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.QualifiedNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class NameChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snCalculator;
	private final IASTNodeChangeCalculator qnCalculator;


	@Inject
	public NameChangeCalculator(
			@NameAnnotation String changeLevel,
			@SimpleNameAnnotation IASTNodeChangeCalculator snCalculator,
			@QualifiedNameAnnotation IASTNodeChangeCalculator qnCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.snCalculator = snCalculator;
		this.qnCalculator = qnCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		if(!pair.areASTNodeTypesSame())
			return changeBuilder.createUnknownChange(pair);
		
		if(pair.getNodeBefore().getNodeType() == ASTNode.SIMPLE_NAME)
		{
			return snCalculator.CalculateASTNodeChange(pair);
		} else {
			return qnCalculator.CalculateASTNodeChange(pair);
		}
	}
}
