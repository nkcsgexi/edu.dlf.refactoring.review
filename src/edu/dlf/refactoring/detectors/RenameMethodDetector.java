package edu.dlf.refactoring.detectors;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.utils.XList;

public class RenameMethodDetector implements IRefactoringDetector{

	private CascadeChangeCriteriaBuilder searchCriteriaBuilder;

	@Inject
	public RenameMethodDetector(
			@SimpleNameAnnotation String snChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel,
			@MethodInvocationAnnotation String miChangeLevel)
	{
		this.searchCriteriaBuilder = new CascadeChangeCriteriaBuilder();
		this.searchCriteriaBuilder.addNextChangeCriteria(mdChangeLevel, SourceChangeType.PARENT);
		this.searchCriteriaBuilder.addNextChangeCriteria(snChangeLevel, SourceChangeType.UPDATE);
		
	}
	

	
	@Override
	public XList<IRefactoring> detectRefactoring(ISourceChange change) {
		// TODO Auto-generated method stub
		return null;
	}

}
