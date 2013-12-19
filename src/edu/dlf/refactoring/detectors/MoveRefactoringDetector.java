package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNodeMapperUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.DetectedMoveRefactoring;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class MoveRefactoringDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final String compilationUnitLV;
	private final ChangeCriteriaBuilder builder;
	private final String methodDeclarationLV;
	private final String fieldDeclarationLV;
	private final F<ISourceChange, ASTNode> getBeforeNode;
	private final F<ISourceChange, ASTNode> getAfterNode;
	private final F<String, IChangeSearchCriteria> getAddCriteria;
	private final F<String, IChangeSearchCriteria> getRemoveCriteria;
	private final F2<List<ISourceChange>,IChangeSearchCriteria,List<ISourceChange>> 
		getLowestChanges;

	@Inject
	public MoveRefactoringDetector(
			Logger logger,
			ChangeCriteriaBuilder exbuilder,
			@CompilationUnitAnnotation String cuLevel,
			@MethodDeclarationAnnotation String mdLevel,
			@FieldDeclarationAnnotation String fLevel) {
		this.logger = logger;
		this.compilationUnitLV = cuLevel;
		this.methodDeclarationLV = mdLevel;
		this.builder = exbuilder;
		this.fieldDeclarationLV = fLevel;
		
		this.getAddCriteria = new F<String, IChangeSearchCriteria>(){
			@Override
			public IChangeSearchCriteria f(String level) {
				builder.reset();
				return builder.addSingleChangeCriteria
					(level, SourceChangeType.ADD).getSearchCriteria();
		}};

		this.getRemoveCriteria = new F<String, IChangeSearchCriteria>(){
			@Override
			public IChangeSearchCriteria f(String level) {
				builder.reset();
				return builder.addSingleChangeCriteria
					(level, SourceChangeType.REMOVE).getSearchCriteria();
		}};
				
		this.getLowestChanges = new F2<List<ISourceChange>, 
				IChangeSearchCriteria, List<ISourceChange>>(){
			@Override
			public List<ISourceChange> f(final List<ISourceChange> cuChanges, 
				final IChangeSearchCriteria criteria) {
				return cuChanges.bind(new F<ISourceChange, List<ISourceChange>>() {
					@Override
					public List<ISourceChange> f(ISourceChange change) {
						List<IChangeSearchResult> results = criteria.search(change);
						return results.map(new F<IChangeSearchResult, ISourceChange>() {
							@Override
							public ISourceChange f(IChangeSearchResult result) {
								return result.getSourceChanges().reverse().head();
		}});}});}};
		
		this.getBeforeNode = new F<ISourceChange, ASTNode>() {
				@Override
				public ASTNode f(ISourceChange change) {
					return change.getNodeBefore();
		}};
			
		this.getAfterNode = new F<ISourceChange, ASTNode>() {
				@Override
				public ASTNode f(ISourceChange change) {
					return change.getNodeAfter();
		}};
	}
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		final List<ISourceChange> cuChanges = SourceChangeUtils.getSelfAndDescendent
			(change).filter(new F<ISourceChange, Boolean>() {
			@Override
			public Boolean f(ISourceChange child) {
				return child.getSourceChangeLevel() == compilationUnitLV;
		}});
		return detectMoveRefactoring(cuChanges, methodDeclarationLV, ASTAnalyzer.
			getMethodDeclarationNamesEqualFunc());
	}

	private List<IDetectedRefactoring> detectMoveRefactoring(
			final List<ISourceChange> cuChanges,
			final String changeLevel,
			final F2<ASTNode, ASTNode, Boolean> areNodesSame) {
		try{
		List<ASTNode> addedDec = getLowestChanges.f(cuChanges, getAddCriteria.
			f(changeLevel)).map(getAfterNode);
		List<ASTNode> removedDec = getLowestChanges.f(cuChanges, getRemoveCriteria.
			f(changeLevel)).map(getBeforeNode);
		return ASTNodeMapperUtils.getSameNodePairs(removedDec, addedDec, 
			areNodesSame).map(new F<P2<ASTNode, ASTNode>, IDetectedRefactoring>() {
				@Override
				public IDetectedRefactoring f(P2<ASTNode, ASTNode> p) {
					return new DetectedMoveRefactoring(RefactoringType.
						Move, p._1(), p._2());
		}});
		}catch(Exception e)
		{
			logger.fatal(e);
			return List.nil();
		}
	}

}
