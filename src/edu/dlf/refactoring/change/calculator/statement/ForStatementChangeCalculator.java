package edu.dlf.refactoring.change.calculator.statement;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ForStatement;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ForStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class ForStatementChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator exCalculator;
	private final IASTNodeChangeCalculator bCalculator;
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	@Inject
	public ForStatementChangeCalculator(
			@ForStatementAnnotation String changeLevel,
			@ExpressionAnnotation IASTNodeChangeCalculator exCalculator,
			@BlockAnnotation IASTNodeChangeCalculator bCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.exCalculator = exCalculator;
		this.bCalculator = bCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {	
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		try {
			SubChangeContainer container = changeBuilder.createSubchangeContainer();
			XList<ASTNode>[] lists = pair.selectChildrenByDescriptor(ForStatement.INITIALIZERS_PROPERTY);
			container.addMultiSubChanges((new SimilarityASTNodeMapStrategy(ASTAnalyzer.
				getASTNodeCompleteDistanceCalculator()).
				map(lists[0], lists[1]).select(new Function<ASTNodePair, ISourceChange>(){
					@Override
					public ISourceChange apply(ASTNodePair arg0) {
						return exCalculator.CalculateASTNodeChange(arg0);
					}})));
			container.addSubChange(exCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor(
					ForStatement.EXPRESSION_PROPERTY)));
			lists = pair.selectChildrenByDescriptor(ForStatement.UPDATERS_PROPERTY);
			container.addMultiSubChanges(new SimilarityASTNodeMapStrategy(ASTAnalyzer.
					getASTNodeCompleteDistanceCalculator()).
				map(lists[0], lists[1]).select(new Function<ASTNodePair, ISourceChange>(){
					@Override
					public ISourceChange apply(ASTNodePair arg0) {
						return exCalculator.CalculateASTNodeChange(arg0);
					}}));
			container.addSubChange(bCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor
					(ForStatement.BODY_PROPERTY)));
			return container;
		} catch (Exception e) {
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}

}
