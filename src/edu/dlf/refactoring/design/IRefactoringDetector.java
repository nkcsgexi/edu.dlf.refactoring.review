package edu.dlf.refactoring.design;

import fj.data.List;

public interface IRefactoringDetector {
	List<IRefactoring> detectRefactoring(ISourceChange change);
}
