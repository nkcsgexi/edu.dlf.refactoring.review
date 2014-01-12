package edu.dlf.refactoring.detectors;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import fj.F;
import fj.data.List;

public abstract class AbstractRefactoringDetector extends IRefactoringDetector{

	protected final F<List<ASTNode>, ASTNode> getASTNodeListHead;

	protected AbstractRefactoringDetector() {
		this.getASTNodeListHead = FJUtils.getHeadFunc((ASTNode)null);
	}
		
	protected ChangeCriteriaBuilder getCascadeCriteriaBuilder()
	{
		return new ChangeCriteriaBuilder();
	}
	
	protected IChangeSearchCriteria getBasicSearchCriteria(final String c, 
			final SourceChangeType t) {
		return new BasicChangeSearchCriteria() {
			@Override
			protected boolean isSourceChangeTypeOk(SourceChangeType type) {
				return type == t;
			}
			
			@Override
			protected boolean isChangeLevelOk(String changeLevel) {
				return changeLevel.equals(c);
			}
	};}
	
}
