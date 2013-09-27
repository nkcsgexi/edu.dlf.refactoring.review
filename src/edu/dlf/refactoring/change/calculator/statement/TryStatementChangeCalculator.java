package edu.dlf.refactoring.change.calculator.statement;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.CatchClauseAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TryStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class TryStatementChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator blCalculator;
	private final IASTNodeChangeCalculator ccCalculator;

	@Inject
	public TryStatementChangeCalculator(
			@TryStatementAnnotation String changeLevel,
			@CatchClauseAnnotation IASTNodeChangeCalculator ccCalculator,
			@BlockAnnotation IASTNodeChangeCalculator blCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.ccCalculator = ccCalculator;
		this.blCalculator = blCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(blCalculator.CalculateASTNodeChange(pair.
				selectByPropertyDescriptor(TryStatement.BODY_PROPERTY)));
		XList[] catches = pair.selectChildrenByDescriptor(TryStatement.CATCH_CLAUSES_PROPERTY);
		container.addMultiSubChanges(new SimilarityASTNodeMapStrategy(new IDistanceCalculator() {
			@Override
			public int calculateDistance(ASTNode before, ASTNode after) {
				String e1 = before.getStructuralProperty(CatchClause.EXCEPTION_PROPERTY).toString();
				String e2 = after.getStructuralProperty(CatchClause.EXCEPTION_PROPERTY).toString();
				return XStringUtils.distance(e1, e2);
			}
		}).map(catches[0], catches[1]).select(new Function<ASTNodePair, ISourceChange>(){
			@Override
			public ISourceChange apply(ASTNodePair arg0) {
				return ccCalculator.CalculateASTNodeChange(arg0);
			}}));
		return container;
	}
	

}
