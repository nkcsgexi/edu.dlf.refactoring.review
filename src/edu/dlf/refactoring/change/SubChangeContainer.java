package edu.dlf.refactoring.change;

import java.util.Collection;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class SubChangeContainer implements ISourceChange{

	private final XList<ISourceChange> subChanges = XList.CreateList();
	private final String changeLevel;
	
	public SubChangeContainer(String changeLevel)
	{
		this.changeLevel = changeLevel;
	}
	
	public void addSubChange(ISourceChange subChange)
	{
		this.subChanges.add(subChange);
	}
	
	
	public void addMultiSubChanges(Collection<ISourceChange> changes)
	{
		this.subChanges.addAll(changes);
	}
	
	public ISourceChange[] getSubSourceChanges()
	{
		return this.subChanges.toArray(new ISourceChange[0]);
	}

	@Override
	public boolean hasSubChanges() {
		return subChanges.any();
	}

	@Override
	public String getSourceChangeLevel() {
		return changeLevel;
	}
}
