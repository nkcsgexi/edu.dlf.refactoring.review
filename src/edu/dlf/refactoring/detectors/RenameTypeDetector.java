package edu.dlf.refactoring.detectors;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import fj.data.List;

public class RenameTypeDetector extends AbstractRefactoringDetector{

	private final CascadeChangeCriteriaBuilder changeDetector;


	@Inject
	public RenameTypeDetector(
			@NameAnnotation String nChangeLevel,
			@TypeDeclarationAnnotation String tdChangeLevel,
			@TypeAnnotation String tChangeLevel)
	{
		this.changeDetector = this.getCascadeCriteriaBuilder();
		this.changeDetector.addSingleChangeCriteria(tChangeLevel, SourceChangeType.PARENT);
		
	}
	
	
	@Override
	public List<IRefactoring> detectRefactoring(ISourceChange change) {
		
		
		
		return List.nil();
	}

}
