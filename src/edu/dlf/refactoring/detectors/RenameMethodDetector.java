package edu.dlf.refactoring.detectors;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.utils.XList;

public class RenameMethodDetector implements IRefactoringDetector{

	
	private final SourceChangeSearcher changeSearcher;

	public RenameMethodDetector(
			@SimpleNameAnnotation String snChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel,
			@MethodInvocationAnnotation String miChangeLevel)
	{
		this.changeSearcher = new SourceChangeSearcher();
		this.changeSearcher.addSearchCriteria(1, snChangeLevel, SourceChangeType.UPDATE);
		this.changeSearcher.addSearchCriteria(1, mdChangeLevel, SourceChangeType.PARENT);
		this.changeSearcher.addSearchCriteria(1, miChangeLevel, SourceChangeType.PARENT);
	}
	

	
	@Override
	public XList<IRefactoring> detectRefactoring(ISourceChange change) {
		// TODO Auto-generated method stub
		return null;
	}

}
