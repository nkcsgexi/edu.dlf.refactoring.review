package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.utils.XList;

public class ExtractMethodDetector implements IRefactoringDetector {

	private final SourceChangeSearcher changeSearcher;
	private final String type;

	public ExtractMethodDetector(
			@ExtractMethod String type,
			@StatementAnnotation String stChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel)
	{
		this.type = type;
		this.changeSearcher = new SourceChangeSearcher();
		this.changeSearcher.addSearchCriteria(stChangeLevel, SourceChangeType.REMOVE);
		this.changeSearcher.addSearchCriteria(mdChangeLevel, SourceChangeType.ADD);
	}
	
	@Override
	public XList<IRefactoring> detectRefactoring(ISourceChange change) {
		XList<IChangeSearchResult> results = changeSearcher.searchChanges(change);
		XList<ISourceChange> stRemoves = results.get(0).getSourceChanges();
		XList<ISourceChange> mdAdds = results.get(1).getSourceChanges();
		if(stRemoves.empty() && mdAdds.empty())
			return XList.CreateList();
		
		
		
		return null;
	}
	
}
