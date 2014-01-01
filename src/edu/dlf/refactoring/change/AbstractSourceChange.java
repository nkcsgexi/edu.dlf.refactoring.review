package edu.dlf.refactoring.change;

import java.util.Collection;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.IJavaElementPair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.Equal;
import fj.F;
import fj.data.List;

public abstract class AbstractSourceChange implements ISourceChange{

	private final ASTNode nodeBefore;
	private final ASTNode nodeAfter;
	private final String changeLevel;
	private final IJavaElement elementAfter;
	private final IJavaElement elementBefore;
	private ISourceChange parent;
	
	private List<ISourceChange> subChanges = List.nil();
	private final Equal<ISourceChange> sourceRefEq = FJUtils.getReferenceEq
		((ISourceChange)null);
	
	protected AbstractSourceChange(String changeLevel, IASTNodePair pair) {
		this.changeLevel = changeLevel;
		this.nodeBefore = pair.getNodeBefore();
		this.nodeAfter = pair.getNodeAfter();
		this.elementBefore = null;
		this.elementAfter = null;
	}
	
	protected AbstractSourceChange(String changeLevel, IJavaElementPair pair) {
		this.changeLevel = changeLevel;
		this.elementBefore = pair.getElementBefore();
		this.elementAfter = pair.getElementAfter();
		this.nodeAfter = null;
		this.nodeBefore = null;
	}
	
	public abstract SourceChangeType getSourceChangeType();
	
	public final void addSubChange(ISourceChange subChange)
	{
		((AbstractSourceChange)subChange).setParentChange(this);
		subChanges = subChanges.snoc(subChange);
	}
	
	
	public final void addMultiSubChanges(Collection<ISourceChange> changes)
	{
		for(ISourceChange c : changes)
		{
			subChanges = subChanges.snoc(c);
		}
	}
	
	public final ISourceChange[] getSubSourceChanges()
	{
		return subChanges.toCollection().toArray(new ISourceChange[0]);
	}
	
	
	public final Void removeSubChanges(Collection<ISourceChange> toRemove)
	{	
		for(ISourceChange remove : toRemove) {
			F<ISourceChange, Boolean> filter = sourceRefEq.eq(remove);
			subChanges = subChanges.removeAll(filter);
		}
		return null;
	}

	public boolean hasSubChanges() {
		return subChanges.length() > 0;
	}

	
	
	@Override
	public final String getSourceChangeLevel()
	{
		return this.changeLevel;
	}
	
	@Override
	public final ISourceChange getParentChange() {
		return this.parent;
	}
	
	@Override
	public final ASTNode getNodeBefore()
	{
		return this.nodeBefore;
	}
	
	@Override
	public final ASTNode getNodeAfter()
	{
		return nodeAfter;
	}
	
	@Override
	public final IJavaElement getElementBefore()
	{
		return this.elementBefore;
	}
	
	@Override
	public final IJavaElement getElementAfter()
	{
		return this.elementAfter;
	}
	
	
	private final void setParentChange(ISourceChange parent) {
		this.parent = parent;
	}
}
