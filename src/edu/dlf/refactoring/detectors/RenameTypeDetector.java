package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.refactorings.DetectedRenameTypeRefactoring;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;

public class RenameTypeDetector extends AbstractRefactoringDetector{

	private final IChangeSearchCriteria typeDeclarationCriteria;
	private final IChangeSearchCriteria typeReferenceCriteria;
	private final Logger logger;

	@Inject
	public RenameTypeDetector(
			Logger logger,
			@NameAnnotation String nChangeLevel,
			@TypeDeclarationAnnotation String tdChangeLevel,
			@SimpleNameAnnotation String snChangeLevel,
			@TypeAnnotation String tChangeLevel)
	{
		this.logger = logger;
		ChangeCriteriaBuilder changeBuilder = this.getCascadeCriteriaBuilder();
		this.typeDeclarationCriteria = changeBuilder.addSingleChangeCriteria
			(tdChangeLevel, SourceChangeType.PARENT).addSingleChangeCriteria
				(snChangeLevel, SourceChangeType.UPDATE).getSearchCriteria();
		changeBuilder.reset();
		this.typeReferenceCriteria = changeBuilder.addSingleChangeCriteria(
			tChangeLevel, SourceChangeType.PARENT).addSingleChangeCriteria(
				snChangeLevel, SourceChangeType.UPDATE).getSearchCriteria();
	}
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		List<ISourceChange> decChanges = this.typeDeclarationCriteria.
			search(change).map(SourceChangeUtils.getLeafSourceChangeFunc());
		List<ISourceChange> refChanges = this.typeReferenceCriteria.
			search(change).map(SourceChangeUtils.getLeafSourceChangeFunc());
		if(decChanges.isEmpty() && refChanges.isEmpty())
			return List.nil();
		Ord<ISourceChange> sorter = Ord.stringOrd.comap(RenameDetectionUtils.
			getBeforeAndAfterKeyFunc());
		Equal<ISourceChange> grouper = Equal.stringEqual.comap(RenameDetectionUtils.
			getBeforeAndAfterKeyFunc());
		List<List<ISourceChange>> groupedNameChanges = decChanges.append(refChanges).
			sort(sorter).group(grouper);
		return groupedNameChanges.map(new F<List<ISourceChange>, 
			IDetectedRefactoring>() {
			@Override
			public IDetectedRefactoring f(List<ISourceChange> changes) {
				return new DetectedRenameTypeRefactoring(changes.map(SourceChangeUtils.
					getNodeBeforeFunc()), changes.map(SourceChangeUtils.
						getNodeAfterFunc()));
		}});
	}

}
