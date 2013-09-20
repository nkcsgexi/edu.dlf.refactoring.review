package edu.dlf.refactoring.implementer;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import fj.data.Option;

public class RenameTypeImplementer implements IRefactoringImplementer{

	@Override
	public Option<IImplementedRefactoring> implementRefactoring
		(IDetectedRefactoring refactoring) {
		return null;
	}

}
