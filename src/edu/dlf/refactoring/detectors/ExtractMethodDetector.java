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
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P2;
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

		final F<IChangeSearchResult, ISourceChange> getLeafChange = 
			new F<SourceChangeSearcher.IChangeSearchResult, ISourceChange>() {
			@Override
			public ISourceChange f(IChangeSearchResult result) {
				return result.getSourceChanges().reverse().head();
			}
		};
		
		final F<ASTNode, Boolean> findType = new F<ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode node) {
				return node.getNodeType() == ASTNode.TYPE_DECLARATION;
			}
		};
				
		final F<P2<IChangeSearchResult, IChangeSearchResult>, Boolean> filter = 
			new F<P2<IChangeSearchResult,IChangeSearchResult>, Boolean>() {
			@Override
			public Boolean f(P2<IChangeSearchResult, IChangeSearchResult> pair) {
				ASTNode type1 = SourceChangeUtils.getEffectedASTNodes
					(getLeafChange.f(pair._1())).find(findType).some();
				ASTNode type2 = SourceChangeUtils.getEffectedASTNodes
					(getLeafChange.f(pair._2())).find(findType).some();
				return (type1 == null || type2 == null) ? false : ASTAnalyzer.
					areTypesSame(type1, type2);
			}
		};
		
		final Equal<P2<IChangeSearchResult, IChangeSearchResult>> eq = 
			Equal.equal(new F<P2<IChangeSearchResult, IChangeSearchResult>, 
			F<P2<IChangeSearchResult, IChangeSearchResult>, Boolean>>() {
				@Override
				public F<P2<IChangeSearchResult, IChangeSearchResult>, Boolean> f(
					final P2<IChangeSearchResult, IChangeSearchResult> pair1) {
					return new F<P2<IChangeSearchResult,IChangeSearchResult>, Boolean>() {
						@Override
						public Boolean f(final P2<IChangeSearchResult, 
							IChangeSearchResult> pair2) {
							ASTNode type11 = SourceChangeUtils.getEffectedASTNodes
								(getLeafChange.f(pair1._1())).find(findType).some();
							ASTNode type22 = SourceChangeUtils.getEffectedASTNodes
								(getLeafChange.f(pair2._2())).find(findType).some();
							return ASTAnalyzer.areTypesSame(type11, type22);
						}};}
		});
		
		
		final F<List<P2<IChangeSearchResult, IChangeSearchResult>>, P2<List<IChangeSearchResult>, 
			List<IChangeSearchResult>>> grouper = new F<List<P2<IChangeSearchResult,
			IChangeSearchResult>>, P2<List<IChangeSearchResult>,List<IChangeSearchResult>>>() {	
				@Override
				public P2<List<IChangeSearchResult>, List<IChangeSearchResult>> f(
						List<P2<IChangeSearchResult, IChangeSearchResult>> list) {
					List<IChangeSearchResult> subList1 = list.map(
						new F<P2<IChangeSearchResult,IChangeSearchResult>, 
					IChangeSearchResult>() {
					@Override
					public IChangeSearchResult f(P2<IChangeSearchResult, 
						IChangeSearchResult> p) {
						return p._1();
					}});
					List<IChangeSearchResult> subList2 = list.map(new F<P2
						<IChangeSearchResult,IChangeSearchResult>, 
						IChangeSearchResult>() {
						@Override
						public IChangeSearchResult f(P2<IChangeSearchResult, 
								IChangeSearchResult> p) {
							return p._2();
						}
					});	
					return List.single(subList1).zip(List.single(subList2)).head();
				}
			};
		
		final List<P2<IChangeSearchResult, IChangeSearchResult>> multiplied = 
			staChanges.bind(mdChanges, new F2<IChangeSearchResult, 
			IChangeSearchResult, P2<IChangeSearchResult, 
			IChangeSearchResult>>(){
				@Override
				public P2<IChangeSearchResult, IChangeSearchResult> f(
						IChangeSearchResult result1, IChangeSearchResult result2) {
					return List.single(result1).zip(List.single(result2)).head();
				}});
	
		return multiplied.filter(filter).group(eq).map(grouper).bind(new
				F<P2<List<IChangeSearchResult>,List<IChangeSearchResult>>, 
				List<IDetectedRefactoring>>() {
					@Override
					public List<IDetectedRefactoring> f(
							P2<List<IChangeSearchResult>, List<IChangeSearchResult>> pair) {
						return detectExtractMethodInType(pair._1(), pair._2());
					}
		});
	}
	

	private List<IDetectedRefactoring> detectExtractMethodInType(
			List<IChangeSearchResult> statementRemoves,
			List<IChangeSearchResult> methodDecAdds) {
		if(statementRemoves.isEmpty() || methodDecAdds.isEmpty())
			return List.nil();
		try {
 			List<ASTNodePair> pairs = new StatementToMethodMapper().map(
				 statementRemoves.bind(new F<IChangeSearchResult, List<ASTNode>>(){
					@Override
					public List<ASTNode> f(IChangeSearchResult result) {
						return result.getSourceChanges().map(new F<ISourceChange, ASTNode>(){
							@Override
							public ASTNode f(ISourceChange change) {
								return change.getNodeBefore();
							}});
					}}),
				methodDecAdds.bind(new F<IChangeSearchResult, List<ASTNode>>(){
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
				return new DetectedExtractMethodRefactoring(list.map(new F<ASTNodePair, ASTNode>(){
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
