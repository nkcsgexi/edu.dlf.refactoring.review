package edu.dlf.refactoring.design;

import fj.F;
import fj.data.List;

public abstract class IRefactoringDetector extends F<ISourceChange, List<IDetectedRefactoring>>{
	public abstract List<IDetectedRefactoring> f(ISourceChange change);
}
