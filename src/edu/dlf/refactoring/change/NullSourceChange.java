package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.AbstractSourceChange;

public class NullSourceChange extends AbstractSourceChange{

	
	public NullSourceChange(String changeLevel)
	{
		super(changeLevel, null, null);
	}
	
	@Override
	public boolean hasSubChanges() {
		return false;
	}

	@Override
	public ISourceChange[] getSubSourceChanges() {
		return new ISourceChange[0];
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.NULL;
	}
}
	
	


