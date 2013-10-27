package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import fj.F;
import fj.data.List;

public class ExtractIntefaceDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final IChangeSearchCriteria addCUCriteria;
	private final IChangeSearchCriteria addTypeCriteria;

	@Inject
	public ExtractIntefaceDetector(
			Logger logger,
			@TypeDeclarationAnnotation String tdChange,
			@CompilationUnitAnnotation String cuChange)
	{
		this.logger = logger;
		ChangeCriteriaBuilder builder = new ChangeCriteriaBuilder();
		this.addTypeCriteria = builder.addSingleChangeCriteria(tdChange, 
			SourceChangeType.ADD).getSearchCriteria();
		builder.reset();
		this.addCUCriteria = builder.addSingleChangeCriteria(cuChange, 
			SourceChangeType.ADD).getSearchCriteria();
		
	}
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {		
		F<ASTNode, List<ASTNode>> getTypeFunc = ASTAnalyzer.getDecendantFunc().
			f(ASTNode.TYPE_DECLARATION);
		List<ASTNode> addedTypes = addTypeCriteria.search(change).map(
			SourceChangeUtils.getLeafSourceChangeFunc()).map(SourceChangeUtils.
				getNodeAfterFunc()).append(addCUCriteria.search(change).map(
					SourceChangeUtils.getLeafSourceChangeFunc()).map(SourceChangeUtils.
						getNodeAfterFunc()).bind(getTypeFunc));
		
		
		
		
		
		return List.nil();
	}

}
