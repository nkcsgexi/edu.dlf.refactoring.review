package edu.dlf.refactoring.ui;

import org.eclipse.core.resources.IProject;

public interface IManualRefactoringInterface {

	void provideInput(IProject before, IProject after, 
		IManualRefactoringCallback callback);
	
	
}
