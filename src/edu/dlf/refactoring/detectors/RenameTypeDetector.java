package edu.dlf.refactoring.detectors;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class RenameTypeDetector implements IRefactoringDetector{

	@Inject
	public RenameTypeDetector()
	{
		
	}
	
	
	@Override
	public XList<IRefactoring> detectRefactoring(ISourceChange change) {
		
		
		
		return null;
	}

}
