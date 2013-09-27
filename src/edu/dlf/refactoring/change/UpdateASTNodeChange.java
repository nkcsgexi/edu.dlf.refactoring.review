package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.AbstractSourceChange;

public class UpdateASTNodeChange extends AbstractSourceChange
{

	public UpdateASTNodeChange(ASTNodePair pair, String changeLevel)
	{
		super(changeLevel, pair.getNodeBefore(), pair.getNodeAfter());
	}
	
	@Override
	public boolean hasSubChanges() {
		return false;
	}

	@Override
	public ISourceChange[] getSubSourceChanges() {
		return new ISourceChange[]{};
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.UPDATE;
	}
}
