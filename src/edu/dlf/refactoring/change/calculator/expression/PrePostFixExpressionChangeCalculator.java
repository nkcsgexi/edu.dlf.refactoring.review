package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.PrePostFixExpressionAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class PrePostFixExpressionChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator exCalculator;

	@Inject
	public PrePostFixExpressionChangeCalculator(
			@PrePostFixExpressionAnnotation String changeLevel,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.exCalculator = exCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer();
		container.addSubChange(exCalculator.CalculateASTNodeChange(pair.select(new Function<ASTNode, ASTNode>(){
			@Override
			public ASTNode apply(ASTNode node) {
				return (ASTNode) (node.getNodeType() == ASTNode.PREFIX_EXPRESSION ? node.getStructuralProperty
						(PrefixExpression.OPERAND_PROPERTY) : node.getStructuralProperty
							(PostfixExpression.OPERAND_PROPERTY));
			}})));
		return container;
	}

}
