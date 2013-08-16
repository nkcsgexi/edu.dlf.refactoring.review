package edu.dlf.refactoring.detectors;

import java.util.Collection;
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
import edu.dlf.refactoring.change.calculator.IASTNodeMapStrategy;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.ExtractMethodRefactoring;
import edu.dlf.refactoring.utils.IEqualityComparer;
import edu.dlf.refactoring.utils.XList;

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
	public XList<IRefactoring> detectRefactoring(ISourceChange change) {
		
		XList<IChangeSearchResult> staChanges = this.statementSearchCriteria.getChangesMeetCriteria(change);
		XList<IChangeSearchResult> mdChanges = this.methodSearchCriteria.getChangesMeetCriteria(change);
		if(staChanges.empty() || mdChanges.empty())
			return XList.CreateList();
		
		 try {
 			XList<ASTNodePair> pairs = new StatementToMethodMapper().map(
				 staChanges.selectMany(new Function<IChangeSearchResult, Collection<ASTNode>>(){
					@Override
					public Collection<ASTNode> apply(IChangeSearchResult result) {
						return result.getSourceChanges().select(new Function<ISourceChange, ASTNode>(){
							@Override
							public ASTNode apply(ISourceChange change) {
								return change.getNodeBefore();
							}});
					}}),
				mdChanges.selectMany(new Function<IChangeSearchResult, Collection<ASTNode>>(){
					@Override
					public Collection<ASTNode> apply(IChangeSearchResult result) {
						return result.getSourceChanges().select(new Function<ISourceChange, ASTNode>(){
							@Override
							public ASTNode apply(ISourceChange change) {
								return change.getNodeAfter();
							}});
					}}));	
 			return createRefactorings(pairs);
		} catch (Exception e) {
			logger.fatal(e);
			return XList.CreateList();
		}
	}
	
	private XList<IRefactoring> createRefactorings(XList<ASTNodePair> pairs) {
		return pairs.groupBy(new IEqualityComparer<ASTNodePair>() {
			@Override
			public boolean AreEqual(ASTNodePair a, ASTNodePair b) {
				return a.getNodeAfter() == b.getNodeAfter();
			}
		}).select(new Function<XList<ASTNodePair>, IRefactoring>(){
			@Override
			public IRefactoring apply(XList<ASTNodePair> list) {		
				return new ExtractMethodRefactoring(list.select(
					new Function<ASTNodePair, ASTNode>(){
					@Override
					public ASTNode apply(ASTNodePair pair) {
						return pair.getNodeBefore();
					}}), list.first().getNodeAfter());
			}});
	}

	private class StatementToMethodMapper implements IASTNodeMapStrategy
	{
		@Override
		public XList<ASTNodePair> map(XList<ASTNode> statements, final XList<ASTNode> 
				methods) throws Exception {
			if(methods.size() == 1)
			{
				return statements.select(new Function<ASTNode, ASTNodePair>(){
					@Override
					public ASTNodePair apply(ASTNode state) {
						return new ASTNodePair(state, methods.first());
					}});
			}
			return statements.select(new Function<ASTNode, ASTNodePair>(){
				@Override
				public ASTNodePair apply(final ASTNode st) {
					return new ASTNodePair(st, methods.min(new Comparator<ASTNode>(){
						@Override
						public int compare(ASTNode m1, ASTNode m2) {
							return findClosestStatementDistance(m1, st) - 
									findClosestStatementDistance(m2, st);
						}}));
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
