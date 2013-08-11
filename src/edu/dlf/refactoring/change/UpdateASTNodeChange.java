package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class UpdateASTNodeChange  extends ASTNodePair implements ISourceChange{

	private final String changeLevel;

	public UpdateASTNodeChange(ASTNodePair pair, String changeLevel)
	{
		super(pair.getNodeBefore(), pair.getNodeAfter());
		this.changeLevel = changeLevel;
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
	public String getSourceChangeLevel() {
		return changeLevel;
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.UPDATE;
	}

}
