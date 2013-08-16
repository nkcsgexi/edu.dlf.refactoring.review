package edu.dlf.refactoring.detectors;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;
import fj.data.List;

public class RenameTypeDetector implements IRefactoringDetector{

	@Inject
	public RenameTypeDetector()
	{
		
	}
	
	
	@Override
	public List<IRefactoring> detectRefactoring(ISourceChange change) {
		
		
		
		return null;
	}

}
