package edu.dlf.refactoring.ui;

public interface IManualRefactoringPosition {
	String getPath();
	int getStartOffset();
	int getLength();
}