package edu.dlf.refactoring.checkers;

import edu.dlf.refactoring.design.IDetectedRefactoring;

public class DefaultCheckingResult implements ICheckingResult
{
	private final IDetectedRefactoring refactoring;
	private final boolean preserve;

	public DefaultCheckingResult(boolean preserve, IDetectedRefactoring 
			refactoring)
	{
		this.preserve = preserve;
		this.refactoring = refactoring;
	}
	
	@Override
	public boolean IsBehaviorPreserving() {
		return this.preserve;
	}

	@Override
	public IDetectedRefactoring getDetectedRefactoring() {
		return this.refactoring;
	}
	
}