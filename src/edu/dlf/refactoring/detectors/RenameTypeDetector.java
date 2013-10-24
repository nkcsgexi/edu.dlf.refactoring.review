package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
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
			(tChangeLevel, SourceChangeType.PARENT).addSingleChangeCriteria
				(snChangeLevel, SourceChangeType.UPDATE).getSearchCriteria();
		this.typeReferenceCriteria = null;
	}
	
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		
		
		
		return List.nil();
	}

}
