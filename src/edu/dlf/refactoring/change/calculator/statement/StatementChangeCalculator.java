package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ExpressionStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.AbstractGeneralChangeCalculator;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.BreakStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ContinueStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.DoStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ForStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.IfStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ReturnStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ThrowStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TryStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.WhileStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F2;
import fj.P2;
import fj.data.List;

public class StatementChangeCalculator extends AbstractGeneralChangeCalculator {

	private final IASTNodeChangeCalculator blockCalculator;
	private final IASTNodeChangeCalculator ifCalculator;
	private final IASTNodeChangeCalculator expressionCalculator;
	private final IASTNodeChangeCalculator fsCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator dsCalculator;
	private final IASTNodeChangeCalculator wsCalculator;
	private final IASTNodeChangeCalculator tsCalculator;
	private final IASTNodeChangeCalculator bsCalculator;
	private final IASTNodeChangeCalculator csCalculator;
	private final IASTNodeChangeCalculator rsCalculator;
	private final IASTNodeChangeCalculator thsCalculator;
	private final IASTNodeChangeCalculator varDecStaCal;
	private final Logger logger;
	
	@Inject
	public StatementChangeCalculator(
			Logger logger,
			@StatementAnnotation String changeLevel,
			@IfStatementAnnotation IASTNodeChangeCalculator ifCalculator,
			@ForStatementAnnotation IASTNodeChangeCalculator fsCalculator,
			@DoStatementAnnotation IASTNodeChangeCalculator dsCalculator,
			@WhileStatementAnnotation IASTNodeChangeCalculator wsCalculator,
			@TryStatementAnnotation IASTNodeChangeCalculator tsCalculator,		
			@BreakStatementAnnotation IASTNodeChangeCalculator bsCalculator,
			@ContinueStatementAnnotation IASTNodeChangeCalculator csCalculator,
			@ReturnStatementAnnotation IASTNodeChangeCalculator rsCalculator,
			@ThrowStatementAnnotation IASTNodeChangeCalculator thsCalculator,
			@BlockAnnotation IASTNodeChangeCalculator blockCalculator, 
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCalculator,
			@VariableDeclarationStatementAnnotation IASTNodeChangeCalculator varDecStaCal) {
		this.logger = logger;
		this.ifCalculator = ifCalculator;
		this.blockCalculator = blockCalculator;
		this.expressionCalculator = expressionCalculator;
		this.fsCalculator = fsCalculator;
		this.wsCalculator = wsCalculator;
		this.dsCalculator = dsCalculator;
		this.tsCalculator = tsCalculator;
		this.bsCalculator = bsCalculator;
		this.csCalculator = csCalculator;
		this.rsCalculator = rsCalculator;
		this.thsCalculator = thsCalculator;
		this.varDecStaCal = varDecStaCal;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	
	private final F2<ASTNode, ASTNode, ISourceChange> statementChangeCalFunc = 
		getChangeCalculationFunc(this);

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		if(pair.getNodeBefore().getNodeType() != pair.getNodeAfter().getNodeType())
		{
			logger.debug("Before statement: " + pair.getNodeBefore());
			logger.debug("After statement: " + pair.getNodeAfter());
			List<List<ASTNode>> groupsBefore = getDecendantStatements.f(pair.
				getNodeBefore()).snoc(pair.getNodeBefore()).group(typeEq);
			List<List<ASTNode>> groupsAfter = getDecendantStatements.f(pair.
				getNodeAfter()).snoc(pair.getNodeAfter()).group(typeEq);
			List<P2<List<ASTNode>, List<ASTNode>>> groupPairs = FJUtils.
				getSamePairs(groupsBefore, groupsAfter, listTypeEq);
			List<P2<ASTNode, ASTNode>> nodePairs = removeSubPairs(groupPairs.bind
				(similarNodeMapper.tuple()).filter(areBothNotNull));
			SubChangeContainer container = changeBuilder.
				createSubchangeContainer(pair);
			container.addMultiSubChanges(nodePairs.sort(orderByFirstNodeStart).
				map(statementChangeCalFunc.tuple()).toCollection());
			container = pruneSourceChangeContainer(container);
			return container == null ? changeBuilder.createUnknownChange(pair) : 
				container ;
		}
		
		switch(pair.getNodeBefore().getNodeType())
		{
		case ASTNode.IF_STATEMENT:
			return ifCalculator.CalculateASTNodeChange(pair);
		case ASTNode.BLOCK:
			return blockCalculator.CalculateASTNodeChange(pair);
		case ASTNode.FOR_STATEMENT:
			return fsCalculator.CalculateASTNodeChange(pair);
		case ASTNode.WHILE_STATEMENT:
			return wsCalculator.CalculateASTNodeChange(pair);
		case ASTNode.DO_STATEMENT:
			return dsCalculator.CalculateASTNodeChange(pair);
		case ASTNode.TRY_STATEMENT:
			return tsCalculator.CalculateASTNodeChange(pair);
		case ASTNode.BREAK_STATEMENT:
			return bsCalculator.CalculateASTNodeChange(pair);	
		case ASTNode.CONTINUE_STATEMENT:
			return csCalculator.CalculateASTNodeChange(pair);	
		case ASTNode.RETURN_STATEMENT:
			return rsCalculator.CalculateASTNodeChange(pair);	
		case ASTNode.THROW_STATEMENT:
			return thsCalculator.CalculateASTNodeChange(pair);	
		case ASTNode.VARIABLE_DECLARATION_STATEMENT:
			return varDecStaCal.CalculateASTNodeChange(pair);
		case ASTNode.EXPRESSION_STATEMENT:
			SubChangeContainer container = this.changeBuilder.createSubchangeContainer(pair);
			container.addSubChange(expressionCalculator.CalculateASTNodeChange(new ASTNodePair(
				(ASTNode)pair.getNodeBefore().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY), 
				(ASTNode)pair.getNodeAfter().getStructuralProperty(ExpressionStatement.EXPRESSION_PROPERTY))));
			return container;
		default:
			return changeBuilder.createUnknownChange(pair);
		}
	}



}
