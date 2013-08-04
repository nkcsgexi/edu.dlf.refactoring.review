package edu.dlf.refactoring.change;

import java.util.ArrayList;
import java.util.List;
import edu.dlf.refactoring.design.ISourceChange;


public abstract class CombinedSourceChange implements ISourceChange
{
	private final List<ISourceChange> _sourceChanges = new ArrayList<ISourceChange>();

	public void AddSourceChange(ISourceChange change)
	{
		this._sourceChanges.add(change);
	}
}

