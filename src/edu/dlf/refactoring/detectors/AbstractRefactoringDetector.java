package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;

public abstract class AbstractRefactoringDetector extends IRefactoringDetector{

	
	protected AbstractRefactoringDetector()
	{
	}
		
	protected ChangeCriteriaBuilder getCascadeCriteriaBuilder()
	{
		return new ChangeCriteriaBuilder();
	}
	
	protected IChangeSearchCriteria getBasicSearchCriteria(final String c, 
			final SourceChangeType t) {
		return new BasicChangeSearchCriteria() {
			@Override
			protected boolean isSourceChangeTypeOk(SourceChangeType type) {
				return type == t;
			}
			
			@Override
			protected boolean isChangeLevelOk(String changeLevel) {
				return changeLevel.equals(c);
			}
	};}
	
}
