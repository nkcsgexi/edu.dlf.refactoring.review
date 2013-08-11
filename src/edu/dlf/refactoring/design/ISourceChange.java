package edu.dlf.refactoring.design;

public interface ISourceChange {
	boolean hasSubChanges();
	ISourceChange[] getSubSourceChanges();
	String getSourceChangeLevel();
	SourceChangeType getSourceChangeType();
	
	
	enum SourceChangeType
	{
		ADD,
		REMOVE,
		UPDATE,
		PARENT,
		UNKNOWN,
		NULL, 
	}
}
