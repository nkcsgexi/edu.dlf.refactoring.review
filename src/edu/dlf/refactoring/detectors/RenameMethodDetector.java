package edu.dlf.refactoring.detectors;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SimpleName;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.RenameMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class RenameMethodDetector extends AbstractRefactoringDetector{
	private final IChangeSearchCriteria declarationChangeCriteira;
	private final IChangeSearchCriteria invocationChangeCriteira;

	@Inject
	public RenameMethodDetector(
			@SimpleNameAnnotation String snChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel,
			@MethodInvocationAnnotation String miChangeLevel)
	{
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
	
	private final boolean areBindingSame(SimpleName name1, SimpleName name2)
	{
		if(name1.resolveBinding() == null || name2.resolveBinding() == null)
			return false;
		return name1.resolveBinding().getKey().equals(name2.resolveBinding().getKey());
	}
	
	@Override
	public List<IRefactoring> detectRefactoring(ISourceChange change) {
		List<IChangeSearchResult> decChanges = this.declarationChangeCriteira.search(change);
		List<IChangeSearchResult> invChanges = this.invocationChangeCriteira.search(change);
		if(decChanges.isEmpty() && invChanges.isEmpty())
			return List.nil();
		Buffer<IRefactoring> buffer = Buffer.empty();
		
		// Rename from a declared method
		return decChanges.append(invChanges).filter(new F<IChangeSearchResult, Boolean>(){
			@Override
			public Boolean f(IChangeSearchResult result) {
				return getLastChangeBeforeName(result).resolveBinding() != null ||
						getLastChangeAfterName(result).resolveBinding() != null;
			}}).group(Equal.equal(new F<IChangeSearchResult, F<IChangeSearchResult, Boolean>>(){
				@Override
				public F<IChangeSearchResult, Boolean> f(final IChangeSearchResult r1) {
					return new F<IChangeSearchResult, Boolean>(){
						@Override
						public Boolean f(IChangeSearchResult r2) {
							return areBindingSame(getLastChangeBeforeName(r1), getLastChangeBeforeName(r2)) ||
									areBindingSame(getLastChangeAfterName(r1), getLastChangeAfterName(r2));
						}};
				}})).map(new F<List<IChangeSearchResult>, IRefactoring>(){
					@Override
					public IRefactoring f(List<IChangeSearchResult> results) {
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
