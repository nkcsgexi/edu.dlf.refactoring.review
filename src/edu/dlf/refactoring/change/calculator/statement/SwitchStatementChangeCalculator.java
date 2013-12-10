package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SwitchStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.AbstractMultipleStatementsChangeCalculator;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchCaseStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SwitchStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.F2;
import fj.data.List;
import fj.data.List.Buffer;

public class SwitchStatementChangeCalculator extends 
	AbstractMultipleStatementsChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator caseCal;
	private final IASTNodeChangeCalculator expressionCal;

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
	}
	
	private final F<ASTNode, List<ASTNode>> getStatementsFunc = 
		ASTNode2ASTNodeUtils.getStructuralPropertyFunc.flip().f(SwitchStatement.
			STATEMENTS_PROPERTY);
	
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
		List<List<ASTNode>> groupsBefore = groupStatementsByLeadingCase
			(getStatementsFunc.f(pair.getNodeBefore()));
		List<List<ASTNode>> groupsAfter = groupStatementsByLeadingCase
			(getStatementsFunc.f(pair.getNodeAfter()));
		container.addMultiSubChanges(groupsBefore.zip(groupsAfter).bind
			(compareCaseGroup.tuple()).toCollection());
		return container;
	}
	
	private F2<List<ASTNode>, List<ASTNode>, List<ISourceChange>> 
		compareCaseGroup = new F2<List<ASTNode>, List<ASTNode>, List
			<ISourceChange>>() {	
			@Override
			public List<ISourceChange> f(List<ASTNode> group1, List<ASTNode> 
				group2) {
				Buffer<ISourceChange> buffer = Buffer.empty();
				buffer.snoc(caseCal.CalculateASTNodeChange(new ASTNodePair
					(group1.head(), group2.head())));
				buffer.append(mapStatementsToPairs(group1.drop(1), group2.drop
					(1)).map(statementCalFunc));
				return buffer.toList();
	}};
	
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
		return results.removeAll(FJUtils.isListEmpty((ASTNode)null));
	}
}
