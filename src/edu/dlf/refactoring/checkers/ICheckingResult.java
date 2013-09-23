package edu.dlf.refactoring.checkers;

import edu.dlf.refactoring.design.IDetectedRefactoring;

public interface ICheckingResult {
	boolean IsBehaviorPreserving();
	IDetectedRefactoring getDetectedRefactoring();
}
