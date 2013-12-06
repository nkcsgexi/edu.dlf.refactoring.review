package edu.dlf.refactoring.change;

import java.util.Collection;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.IJavaElementPair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.AbstractSourceChange;
import fj.Equal;
import fj.F;
import fj.data.List;

public class SubChangeContainer extends AbstractSourceChange{

	private List<ISourceChange> subChanges = List.nil();
	private final Equal<ISourceChange> sourceRefEq = FJUtils.getReferenceEq
		((ISourceChange)null);
	
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
		subChanges = subChanges.snoc(subChange);
	}
	
	
	public void addMultiSubChanges(Collection<ISourceChange> changes)
	{
		for(ISourceChange c : changes)
		{
			subChanges = subChanges.snoc(c);
		}
	}
	
	public ISourceChange[] getSubSourceChanges()
	{
		return subChanges.toCollection().toArray(new ISourceChange[0]);
	}
	
	
	public Void removeSubChanges(Collection<ISourceChange> toRemove)
	{	
		for(ISourceChange remove : toRemove) {
			F<ISourceChange, Boolean> filter = sourceRefEq.eq(remove);
			subChanges = subChanges.removeAll(filter);
		}
		return null;
	}

	@Override
	public boolean hasSubChanges() {
		return subChanges.length() > 0;
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.PARENT;
	}
}
