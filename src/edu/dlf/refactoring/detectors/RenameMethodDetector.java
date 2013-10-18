package edu.dlf.refactoring.detectors;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SimpleName;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.RenameMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;

public class RenameMethodDetector extends AbstractRefactoringDetector{
	private final IChangeSearchCriteria declarationChangeCriteira;
	private final IChangeSearchCriteria invocationChangeCriteira;
	private Logger logger;

	@Inject
	public RenameMethodDetector(
			Logger logger,
			@SimpleNameAnnotation String snChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel,
			@MethodInvocationAnnotation String miChangeLevel)
	{
		this.logger = logger;
		this.declarationChangeCriteira = this.getCascadeCriteriaBuilder().
			addSingleChangeCriteria(mdChangeLevel, SourceChangeType.PARENT)
				.addSingleChangeCriteria(snChangeLevel, SourceChangeType.UPDATE).
					getSearchCriteria();
		this.invocationChangeCriteira = this.getCascadeCriteriaBuilder().
			addSingleChangeCriteria(miChangeLevel, SourceChangeType.PARENT).
				addSingleChangeCriteria(snChangeLevel, SourceChangeType.UPDATE).
					getSearchCriteria();
	}
	
	private final SimpleName getLastChangeBeforeName(IChangeSearchResult result){
		return (SimpleName) result.getSourceChanges().last().getNodeBefore();
	}
	private final SimpleName getLastChangeAfterName(IChangeSearchResult result){
		return (SimpleName) result.getSourceChanges().last().getNodeAfter();
	}

	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		List<IChangeSearchResult> decChanges = this.declarationChangeCriteira.search(change);
		List<IChangeSearchResult> invChanges = this.invocationChangeCriteira.search(change);
		if(decChanges.isEmpty() && invChanges.isEmpty())
			return List.nil();
		
		
		F<IChangeSearchResult, String> getKeyFunc = new F<IChangeSearchResult, 
			String>() {
			@Override
			public String f(IChangeSearchResult result) {
				String key2 = getLastChangeBeforeName(result).resolveBinding().
					getKey() + getLastChangeAfterName(result).resolveBinding().
						getKey();
				return key2;
		}};
		Ord<IChangeSearchResult> sorter = Ord.stringOrd.comap(getKeyFunc);
		Equal<IChangeSearchResult> grouper = Equal.stringEqual.comap(getKeyFunc);
		
		List<List<IChangeSearchResult>> groupedNameChanges = invChanges.
			append(decChanges).sort(sorter).group(grouper);
		
		logger.info(groupedNameChanges.length());
		return groupedNameChanges.map(
			new F<List<IChangeSearchResult>, IDetectedRefactoring>(){
				@Override
				public IDetectedRefactoring f(List<IChangeSearchResult> results) {
					List<ASTNode> namesBefore = results.map(new F<IChangeSearchResult, ASTNode>(){
						@Override
						public ASTNode f(IChangeSearchResult result) {
							return getLastChangeBeforeName(result);
						}});
					List<ASTNode> namesAfter = results.map(new F<IChangeSearchResult, ASTNode>(){
						@Override
						public ASTNode f(IChangeSearchResult result) {
							return getLastChangeAfterName(result);
						}});
					return new RenameMethodRefactoring(namesBefore, namesAfter);				
				}});
	}
}
