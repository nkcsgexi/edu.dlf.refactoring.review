package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.ChangeCriteriaBuilder;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.DetectedRenameLocalVariable;
import fj.F;
import fj.data.List;

public class RenameLocalVariableChecker implements IRefactoringChecker{

	private final Logger logger;
	private final IChangeSearchCriteria searchCriteria;

	@Inject
	public RenameLocalVariableChecker(Logger logger, 
			ChangeCriteriaBuilder builder,
			@SimpleNameAnnotation String simpleNameLv) {
		this.logger = logger;
		this.searchCriteria = builder.addSingleChangeCriteria(simpleNameLv, 
			SourceChangeType.UPDATE).getSearchCriteria();
	}
	
	@Override
	public ICheckingResult checkRefactoring(final IDetectedRefactoring detected,
		final IImplementedRefactoring implemented) {
		List<ISourceChange> nameChanges = implemented.getSourceChanges().bind
			(new F<ISourceChange, List<IChangeSearchResult>>() {
			@Override
			public List<IChangeSearchResult> f(ISourceChange change) {
				return searchCriteria.search(change);
		}}).map(SourceChangeUtils.getLeafSourceChangeFunc());
		if(nameChanges.length() == detected.getEffectedNodeList(
			DetectedRenameLocalVariable.SimpleNamesBefore).length()) {
			logger.info("Rename local variabel is correct.");
			return new DefaultCheckingResult(true, detected);
		}
		return new DefaultCheckingResult(false, detected);
	}
}
