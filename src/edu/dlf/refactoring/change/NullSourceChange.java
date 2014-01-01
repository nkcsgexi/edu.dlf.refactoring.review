package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ASTNodePair;

public class NullSourceChange extends AbstractSourceChange{

	public NullSourceChange(String changeLevel)
	{
		super(changeLevel, new ASTNodePair(null, null));
	}
	
	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.NULL;
	}
}
	
	


