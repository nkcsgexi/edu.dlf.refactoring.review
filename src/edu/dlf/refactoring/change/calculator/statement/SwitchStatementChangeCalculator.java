package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SwitchStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchCaseStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchStatementAnnotation;
import edu.dlf.refactoring.change.AbstractMultipleStatementsChangeCalculator;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class SwitchStatementChangeCalculator extends 
	AbstractMultipleStatementsChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator caseCal;
	private final IASTNodeChangeCalculator expressionCal;
	private final IASTNodeChangeCalculator statementCal;


	@Inject
	public SwitchStatementChangeCalculator(
			Logger logger,
			@SwitchStatementAnnotation String changeLV,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCal,
			@SwitchCaseStatementAnnotation IASTNodeChangeCalculator caseCal,
			@StatementAnnotation IASTNodeChangeCalculator statementCal) {
		super(statementCal);
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.caseCal = caseCal;
		this.expressionCal = expressionCal;
		this.statementCal = statementCal;
	}
	
	private final F<ASTNode, List<ASTNode>> getStatementsFunc = 
		ASTNode2ASTNodeUtils.getStructuralPropertyFunc.flip().f(SwitchStatement.
			STATEMENTS_PROPERTY);
	
	private final F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> 
		similarityMapper = ASTAnalyzer.getASTNodeMapper(6, ASTAnalyzer.
			getDefaultASTNodeSimilarityScore(10));
	
	private final F<ASTNode, Boolean> isCaseStatement = ASTNode2Boolean.
		isASTNodeTypeRight.flip().f(ASTNode.SWITCH_CASE);
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.
			createSubchangeContainer(pair);
		container.addSubChange(expressionCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(SwitchStatement.EXPRESSION_PROPERTY)));
		
		
		return container;
	}
	
	private List<List<ASTNode>> groupStatementsByLeadingCase(final List<ASTNode> 
		statements) {
		List<List<ASTNode>> results = List.nil();
		Buffer<ASTNode> buffer = Buffer.empty();
		for(ASTNode node : statements.toCollection()) {
			if(isCaseStatement.f(node)) {
				results = results.snoc(buffer.toList());
				buffer = Buffer.empty();
			}
			buffer.snoc(node);
		}
		return results.removeAll(FJUtils.isEmpty((ASTNode)null));
	}

}
