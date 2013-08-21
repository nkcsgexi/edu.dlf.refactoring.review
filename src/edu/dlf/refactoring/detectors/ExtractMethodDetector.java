package edu.dlf.refactoring.detectors;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.ExtractMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;

public class ExtractMethodDetector extends AbstractRefactoringDetector {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IChangeSearchCriteria statementSearchCriteria;
	private final IChangeSearchCriteria methodSearchCriteria;
	
	@Inject
	public ExtractMethodDetector(
			@StatementAnnotation String stChangeLevel,
			@ExpressionAnnotation String expChangeLevel,
			@MethodDeclarationAnnotation String mdChangeLevel)
	{
		this.statementSearchCriteria = getBasicSearchCriteria(stChangeLevel, SourceChangeType.REMOVE);
		this.methodSearchCriteria = getBasicSearchCriteria(mdChangeLevel, SourceChangeType.ADD);
	}
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		List<IChangeSearchResult> staChanges = this.statementSearchCriteria.search(change);
		List<IChangeSearchResult> mdChanges = this.methodSearchCriteria.search(change);
		if(staChanges.isEmpty() || mdChanges.isEmpty())
			return List.nil();
		 try {
 			List<ASTNodePair> pairs = new StatementToMethodMapper().map(
				 staChanges.bind(new F<IChangeSearchResult, List<ASTNode>>(){
					@Override
					public List<ASTNode> f(IChangeSearchResult result) {
						return result.getSourceChanges().map(new F<ISourceChange, ASTNode>(){
							@Override
							public ASTNode f(ISourceChange change) {
								return change.getNodeBefore();
							}});
					}}),
				mdChanges.bind(new F<IChangeSearchResult, List<ASTNode>>(){
					@Override
					public List<ASTNode> f(IChangeSearchResult result) {
						return result.getSourceChanges().map(new F<ISourceChange, ASTNode>(){
							@Override
							public ASTNode f(ISourceChange change) {
								return change.getNodeAfter();
							}});
					}}));	
 			return createRefactorings(pairs);
		} catch (Exception e) {
			logger.fatal(e);
			return List.nil();
		}
	}
	
	private List<IDetectedRefactoring> createRefactorings(List<ASTNodePair> pairs) {
		Equal<ASTNodePair> equal = Equal.equal(new F<ASTNodePair, F<ASTNodePair, Boolean>>(){
			@Override
			public F<ASTNodePair, Boolean> f(final ASTNodePair n1) {
				return new F<ASTNodePair, Boolean>(){
					@Override
					public Boolean f(final ASTNodePair n2) {
						return n1.getNodeAfter() == n2.getNodeAfter();
					}};
			}});
		return pairs.group(equal).map(new F<List<ASTNodePair>, IDetectedRefactoring>(){
			@Override
			public IDetectedRefactoring f(List<ASTNodePair> list) {		
				return new ExtractMethodRefactoring(list.map(new F<ASTNodePair, ASTNode>(){
					@Override
					public ASTNode f(ASTNodePair p) {
						return p.getNodeBefore();
					}}), list.head().getNodeAfter());
			}});
	}

	private class StatementToMethodMapper
	{
		public List<ASTNodePair> map(final List<ASTNode> statements, final List<ASTNode> 
				methods) throws Exception {
			if(methods.length() == 1) {
				return statements.map(new F<ASTNode, ASTNodePair>(){
					@Override
					public ASTNodePair f(ASTNode state) {
						return new ASTNodePair(state, methods.head());
					}});
			}
			return statements.map(new F<ASTNode, ASTNodePair>() {
				@Override
				public ASTNodePair f(final ASTNode st) {					
					Ord<ASTNode> ord = Ord.intOrd.comap(new F<ASTNode, Integer>(){
						@Override
						public Integer f(ASTNode method) {
							return findClosestStatementDistance(method, st);
						}});
					return new ASTNodePair(st, methods.minimum(ord));
				}});
		}
		
		private int findClosestStatementDistance(final ASTNode method, final ASTNode state)
		{
			return ASTAnalyzer.getDecendents(method).where(new Predicate<ASTNode>(){
				@Override
				public boolean apply(ASTNode child) {
					return ASTAnalyzer.isStatement(child);
				}}).select(new Function<ASTNode, Integer>(){
					@Override
					public Integer apply(ASTNode node) {
						return ASTAnalyzer.getASTNodeCompleteDistanceCalculator().
								calculateDistance(node, state);
					}}).min(new Comparator<Integer>() {
						@Override
						public int compare(Integer o1, Integer o2) {
							return o1 - o2;
						}
					});
		}
	}
}
