package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class VariableDeclarationFragmentChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator exCalculator;
	private final IASTNodeChangeCalculator snCalculator;

	@Inject
	public VariableDeclarationFragmentChangeCalculator(
			@VariableDeclarationFragmentAnnotation String changeLevel,
			@SimpleNameAnnotation IASTNodeChangeCalculator snCalculator,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator
		)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.snCalculator = snCalculator;
		this.exCalculator = exCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(snCalculator.CalculateASTNodeChange(pair.select(
				new Function<ASTNode, ASTNode>(){
			@Override
			public ASTNode apply(ASTNode node) {
				return ((VariableDeclarationFragment)node).getName();
			}})));
		container.addSubChange(exCalculator.CalculateASTNodeChange(pair.select(
				new Function<ASTNode, ASTNode>(){
			@Override
			public ASTNode apply(ASTNode node) {
				return ((VariableDeclarationFragment)node).getInitializer();
			}})));
		return container;
	}

}
