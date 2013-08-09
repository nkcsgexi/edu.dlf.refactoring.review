package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ISourceChange;

public class NullSourceChange implements ISourceChange{

	private final String changeLevel;

	public NullSourceChange(String changeLevel)
	{
		this.changeLevel = changeLevel;
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
	public String getSourceChangeLevel() {
		return changeLevel;
	}

}
	
	


