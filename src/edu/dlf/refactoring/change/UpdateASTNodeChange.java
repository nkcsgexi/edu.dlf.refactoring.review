package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ASTNodePair;

public class UpdateASTNodeChange extends AbstractSourceChange
{
	public UpdateASTNodeChange(ASTNodePair pair, String changeLevel)
	{
		super(changeLevel, pair);
	}
	
	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.UPDATE;
	}
}
