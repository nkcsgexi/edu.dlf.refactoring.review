package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class ChangeBuilder {
	
	
	private final String changeLevel;

	public ChangeBuilder(String changeLevel)
	{
		this.changeLevel = changeLevel;
	}
	
	private RemoveASTNodeChange createRemoveNodeChange(ASTNode node)
	{
		return new RemoveASTNodeChange(this.changeLevel, node);
	}

	private AddASTNodeChange createAddNodeChange(ASTNode node)
	{
		return new AddASTNodeChange(this.changeLevel, node);
	}

	public UpdateASTNodeChange createUpdateNodesChange(ASTNodePair pair)
	{
		return new UpdateASTNodeChange(pair, changeLevel);
	}

	public SubChangeContainer createSubchangeContainer(IASTNodePair pair)
	{
		return new SubChangeContainer(this.changeLevel, pair);
	}
	
	public NullSourceChange createNullChange()
	{
		return new NullSourceChange(this.changeLevel);
	}
	
	public UnknownSourceChange createUnknownChange(ASTNodePair pair)
	{
		return new UnknownSourceChange(pair.getNodeBefore(), pair.getNodeAfter());
	}


	public ISourceChange buildSimpleChange(ASTNodePair pair) {
		if (pair.getNodeBefore() == null || pair.getNodeAfter() == null) {
			if (pair.getNodeBefore() != null) {
				return createRemoveNodeChange(pair.getNodeBefore());
			} else if(pair.getNodeAfter() != null){
				return createAddNodeChange(pair.getNodeAfter());
			} else 
				return new NullSourceChange(changeLevel);
		}
		return ASTAnalyzer.areASTNodesSame(pair.getNodeBefore(), pair.
				getNodeAfter()) ? new NullSourceChange(changeLevel) : null;
	}
}
