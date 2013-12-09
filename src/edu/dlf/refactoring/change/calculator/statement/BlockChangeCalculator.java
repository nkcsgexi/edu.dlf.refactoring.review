package edu.dlf.refactoring.change.calculator.statement;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.AbstractMultipleStatementsChangeCalculator;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.data.List;

public class BlockChangeCalculator extends AbstractMultipleStatementsChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	
	@Inject
	public BlockChangeCalculator(
			Logger logger,
			@BlockAnnotation String changeLevel,
			@StatementAnnotation IASTNodeChangeCalculator stCalculator) {
		super(stCalculator);
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer
			(pair);
		List<ASTNode> beforeSts = FJUtils.createListFromCollection(((Block) pair.
			getNodeBefore()).statements());
		List<ASTNode> afterSts = FJUtils.createListFromCollection(((Block) pair.
			getNodeAfter()).statements());
		List<ASTNodePair> statementPairs = mapStatementsToPairs(beforeSts, beforeSts);
		container.addMultiSubChanges(statementPairs.map(this.statementCalFunc).
			toCollection());
		return container;
	}


}
