package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.utils.XList;

public class ExtractMethodDetector implements IRefactoringDetector {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IChangeSearchCriteria searchCriteria;

	public ExtractMethodDetector(
			@ExtractMethod String type,
			@StatementAnnotation String stChangeLevel,
			@ExpressionAnnotation String expChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel)
	{
		this.searchCriteria = new CascadeChangeCriteriaBuilder().addNextChangeCriteria(
			mdChangeLevel, SourceChangeType.PARENT).
			addNextChangeCriteria(stChangeLevel, SourceChangeType.REMOVE).getSearchCriteria();
		
	}
	
	@Override
	public XList<IRefactoring> detectRefactoring(ISourceChange change) {
		XList<IChangeSearchResult> results = changeSearcher.searchChanges(change);
		XList<ISourceChange> stRemoves = results.get(0).getSourceChanges();
		XList<ISourceChange> mdAdds = results.get(1).getSourceChanges();
		if(stRemoves.empty() || mdAdds.empty())
			return XList.CreateList();
		
		
		
		
		
		return null;
	}
	
}
