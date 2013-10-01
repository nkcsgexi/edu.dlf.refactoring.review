package edu.dlf.refactoring.change;

import java.util.Collection;

import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.IJavaElementPair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.AbstractSourceChange;
import edu.dlf.refactoring.utils.XList;

public class SubChangeContainer extends AbstractSourceChange{

	private final XList<ISourceChange> subChanges = XList.CreateList();
	
	public SubChangeContainer(String changeLevel, IASTNodePair pair)
	{
		super(changeLevel, pair.getNodeBefore(), pair.getNodeAfter());
	}
	
	public SubChangeContainer(String changeLevel, IJavaElementPair pair)
	{
		super(changeLevel, pair.getElementBefore(), pair.getElementAfter());
	}
	
	public void addSubChange(ISourceChange subChange)
	{
		((AbstractSourceChange) subChange).setParentChange(this);
		this.subChanges.add(subChange);
	}
	
	
	public void addMultiSubChanges(Collection<ISourceChange> changes)
	{
		for(ISourceChange c : changes)
		{
			((AbstractSourceChange) c).setParentChange(this);
		}
		this.subChanges.addAll(changes);
	}
	
	public ISourceChange[] getSubSourceChanges()
	{
		return this.subChanges.toArray(new ISourceChange[0]);
	}
	
	
	public Void removeSubChanges(Collection<ISourceChange> toRemove)
	{
		for(ISourceChange rm : toRemove)
		{
			subChanges.remove(toRemove);
		}
		
		return null;
	}

	@Override
	public boolean hasSubChanges() {
		return subChanges.any();
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.PARENT;
	}
}
