package edu.dlf.refactoring.change;


import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.DlfStringUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public abstract class AbstractMultipleStatementsChangeCalculator implements 
	IASTNodeChangeCalculator{
	
	protected final F<ASTNodePair, ISourceChange> statementCalFunc;

	protected AbstractMultipleStatementsChangeCalculator(IASTNodeChangeCalculator 
		stCalculator) {
		this.statementCalFunc = ASTNodePair.splitPairFunc.andThen
				(SourceChangeUtils.getChangeCalculationFunc(stCalculator).tuple());
	}
	
	private final F<String, String> spaceRemover = new F<String, String>(){
		@Override
		public String f(String text) {
			return DlfStringUtils.RemoveWhiteSpace(text);
	}};
	
	private final F<ASTNode, ASTNodePair> createAddPair = FJUtils.prependElementFunc
		((ASTNode)null, (ASTNode)null).andThen(ASTNodePair.createPairFunc.tuple());
	
	private final F<ASTNode, ASTNodePair> createRemovePair = FJUtils.appendElementFunc
		((ASTNode)null, (ASTNode)null).andThen(ASTNodePair.createPairFunc.tuple());
	
	protected List<ASTNodePair> mapStatementsToPairs(List<ASTNode> beforeSts, 
			List<ASTNode> afterSts) {
		final Buffer<ASTNodePair> container = Buffer.empty();
		final List<String> beforeLines = beforeSts.map(ASTNode2StringUtils.
			astNodeToStringFunc).map(spaceRemover);
		final List<String> afterLines = afterSts.map(ASTNode2StringUtils.
			astNodeToStringFunc).map(spaceRemover);
		final List<Delta> diffs = FJUtils.createListFromCollection(DiffUtils.
			diff(FJUtils.toJavaList(beforeLines), FJUtils.toJavaList(afterLines)).
				getDeltas());
		
		for(Delta diff : diffs) {
			if(diff.getType() == TYPE.CHANGE) {
				int changeCount = Math.min(diff.getOriginal().getLines().size(), 
					diff.getRevised().getLines().size());
				for(int i = 0; i < changeCount; i++) {
					container.snoc(new ASTNodePair(
						beforeSts.index(diff.getOriginal().getPosition() + i),
						afterSts.index(diff.getRevised().getPosition() + i)));
				}
				if(diff.getOriginal().getLines().size() > changeCount) {
					List<ASTNode> removed = FJUtils.getSubList(beforeSts, 
						changeCount, beforeSts.length());
					container.append(removed.map(this.createRemovePair));
				}
				if(diff.getRevised().getLines().size() > changeCount) {
					List<ASTNode> added = FJUtils.getSubList(afterSts, changeCount, 
						afterSts.length());
					container.append(added.map(this.createAddPair));
				}
			}
			else if(diff.getType() == TYPE.DELETE) {
				int start = diff.getOriginal().getPosition();
				int end = start + diff.getOriginal().size();
				List<ASTNode> removed = FJUtils.getSubList(beforeSts, start, end);
				container.append(removed.map(this.createRemovePair));
				
			} else if(diff.getType() == TYPE.INSERT) {
				int start = diff.getRevised().getPosition();
				int end = start + diff.getRevised().size();
				List<ASTNode> added = FJUtils.getSubList(afterSts, start, end);
				container.append(added.map(this.createAddPair));
			}
		}
		return container.toList();
	}

}
