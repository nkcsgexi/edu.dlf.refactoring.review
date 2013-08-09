package edu.dlf.refactoring.design;

public interface ISourceChange {
	boolean hasSubChanges();
	ISourceChange[] getSubSourceChanges();
	String getSourceChangeLevel();
}
