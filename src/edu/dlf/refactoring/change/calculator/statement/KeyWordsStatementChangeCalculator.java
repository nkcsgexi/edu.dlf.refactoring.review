package edu.dlf.refactoring.change.calculator.statement;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BreakStatementAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ContinueStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class KeyWordsStatementChangeCalculator implements IASTNodeChangeCalculator{
	
	private HashMap<Integer, ChangeBuilder> map;

	@Inject
	public KeyWordsStatementChangeCalculator(
			@ContinueStatementAnnotation String changeLevelContinue,
			@BreakStatementAnnotation String changeLevelBreak)
	{
		this.map = new HashMap<Integer, ChangeBuilder>();
		this.map.put(ASTNode.CONTINUE_STATEMENT, new ChangeBuilder
				(changeLevelContinue));
		this.map.put(ASTNode.BREAK_STATEMENT, new ChangeBuilder
				(changeLevelBreak));
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ChangeBuilder builder = this.map.get(pair.getNodeBefore().getNodeType());
		return builder.buildSimpleChange(pair);
	}
}
