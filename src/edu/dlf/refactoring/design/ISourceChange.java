package edu.dlf.refactoring.design;

import java.util.Collection;

public interface ISourceChange extends IASTNodePair, IJavaElementPair{
	
	boolean hasSubChanges();
	ISourceChange[] getSubSourceChanges();
	SourceChangeType getSourceChangeType();
	ISourceChange getParentChange();
	String getSourceChangeLevel();
	void addSubChange(ISourceChange subChange);
	void addMultiSubChanges(Collection<ISourceChange> changes);
	
	
	public enum SourceChangeType {
		ADD,
		REMOVE,
		UPDATE,
		PARENT,
		UNKNOWN,
		NULL, 
	}
}
