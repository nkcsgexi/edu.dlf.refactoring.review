package edu.dlf.refactoring.design;

import edu.dlf.refactoring.utils.XList;

public interface IRefactoringDetector {
	XList<IRefactoring> detectRefactoring(ISourceChange change);
}
