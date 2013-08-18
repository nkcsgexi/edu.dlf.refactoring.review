package edu.dlf.refactoring.detectors;
import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.QualifiedNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.data.List;

public class RenameMethodDetector extends AbstractRefactoringDetector{
	private final IChangeSearchCriteria declarationChangeCriteira;
	private final IChangeSearchCriteria invocationChangeCriteira;

	@Inject
	public RenameMethodDetector(
			@SimpleNameAnnotation String snChangeLevel,
			@QualifiedNameAnnotation String qnChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel,
			@MethodInvocationAnnotation String miChangeLevel)
	{
		this.declarationChangeCriteira = this.getCascadeCriteriaBuilder().
			addSingleChangeCriteria(mdChangeLevel, SourceChangeType.PARENT)
				.addSingleChangeCriteria(snChangeLevel, SourceChangeType.UPDATE).
					getSearchCriteria();
		this.invocationChangeCriteira = this.getCascadeCriteriaBuilder().
			addSingleChangeCriteria(miChangeLevel, SourceChangeType.PARENT).
				addZeroOrMoreChangeCriteria(qnChangeLevel, SourceChangeType.PARENT).
					addSingleChangeCriteria(snChangeLevel, SourceChangeType.UPDATE).
						getSearchCriteria();
	}
	
	@Override
	public List<IRefactoring> detectRefactoring(ISourceChange change) {
		List<IChangeSearchResult> decChanges = this.declarationChangeCriteira.search(change);
		List<IChangeSearchResult> invChanges = this.invocationChangeCriteira.search(change);
		if(decChanges.isEmpty() && invChanges.isEmpty())
			return List.nil();
		
		
		return List.nil();
	}

}
