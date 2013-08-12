package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.MethodInvocation;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class MethodInvocationChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator nCalculator;
	private final IASTNodeChangeCalculator exCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator tCalculator;

	@Inject
	public MethodInvocationChangeCalculator(
			@MethodInvocationAnnotation String changeLevel,
			@TypeAnnotation IASTNodeChangeCalculator tCalculator,
			@NameAnnotation IASTNodeChangeCalculator nCalculator,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.nCalculator = nCalculator;
		this.exCalculator = exCalculator;
		this.tCalculator = tCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer();
		
		container.addSubChange(exCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor(
				MethodInvocation.EXPRESSION_PROPERTY)));
		
		XList[] types = pair.selectChildrenByDescriptor(MethodInvocation.TYPE_ARGUMENTS_PROPERTY);
		container.addMultiSubChanges(new SimilarityASTNodeMapStrategy(ASTAnalyzer.
			getASTNodeCompleteDistanceCalculator()).map(types[0], types[1]).select(
				new Function<ASTNodePair, ISourceChange>(){
					@Override
					public ISourceChange apply(ASTNodePair arg0) {
						return tCalculator.CalculateASTNodeChange(arg0);
					}}));

		container.addSubChange(this.nCalculator.CalculateASTNodeChange(pair.
				selectByPropertyDescriptor(MethodInvocation.NAME_PROPERTY)));
		
		XList[] args = pair.selectChildrenByDescriptor(MethodInvocation.ARGUMENTS_PROPERTY);
		container.addMultiSubChanges(new SimilarityASTNodeMapStrategy(ASTAnalyzer.
				getASTNodeCompleteDistanceCalculator()).
			map(args[0], args[1]).select(new Function<ASTNodePair, ISourceChange>(){
				@Override
				public ISourceChange apply(ASTNodePair arg0) {
					return exCalculator.CalculateASTNodeChange(arg0);
				}}));
		return container;
	}
}
