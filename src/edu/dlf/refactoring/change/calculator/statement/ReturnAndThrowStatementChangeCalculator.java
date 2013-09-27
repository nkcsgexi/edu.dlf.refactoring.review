package edu.dlf.refactoring.change.calculator.statement;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ReturnStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ThrowStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class ReturnAndThrowStatementChangeCalculator implements IASTNodeChangeCalculator {
	private final HashMap<Integer, ChangeBuilder> map;
	private final IASTNodeChangeCalculator exCalculator;

	@Inject
	public ReturnAndThrowStatementChangeCalculator(
			@ReturnStatementAnnotation String returnChangeLevel,
			@ThrowStatementAnnotation String throwChangeLevel,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator)
	{
		this.map = new HashMap<Integer, ChangeBuilder>();
		this.map.put(ASTNode.RETURN_STATEMENT, new ChangeBuilder(returnChangeLevel));
		this.map.put(ASTNode.THROW_STATEMENT, new ChangeBuilder(throwChangeLevel));
		this.exCalculator = exCalculator;
	}

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ChangeBuilder builder = this.map.get(pair.getNodeBefore().getNodeType());
		ISourceChange change = builder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = builder.createSubchangeContainer(pair);
		if(pair.getNodeBefore().getNodeType() == ASTNode.RETURN_STATEMENT) {
			container.addSubChange(exCalculator.CalculateASTNodeChange(pair.
				selectByPropertyDescriptor(ReturnStatement.EXPRESSION_PROPERTY)));
		} else
		{
			container.addSubChange(exCalculator.CalculateASTNodeChange(pair.
				selectByPropertyDescriptor(ThrowStatement.EXPRESSION_PROPERTY)));
		}
		return container;
	}
}
