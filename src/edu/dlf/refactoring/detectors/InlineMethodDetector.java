package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Statement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.analyzers.ASTNode2IntegerUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.refactorings.DetectedInlineMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class InlineMethodDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final F<ASTNode, List<ASTNode>> getDownMethodInvocations = ASTAnalyzer.
		getDecendantFunc().f(ASTNode.METHOD_INVOCATION);
	private final F<ASTNode, List<ASTNode>> getUpMethodDeclaration = ASTAnalyzer.
		getAncestorsFunc.f(ASTNode.METHOD_DECLARATION);
	private final Equal<ASTNode> methodBindingEq = Equal.stringEqual.comap
		(ASTNode2StringUtils.resolveBindingKey);
	private final Equal<ASTNode> rootRefEq = FJUtils.getReferenceEq
		((ASTNode)null).comap(ASTNode2ASTNodeUtils.getRootFunc);
	private final Equal<ASTNode> rootNameEq = Equal.stringEqual.comap(
		ASTNode2ASTNodeUtils.getRootFunc.andThen(ASTNode2StringUtils.
			getCompilationUnitName));
	private final static double threshold = 0.7;
	
	private final F<ISourceChange, List<ASTNode>> removedMethodSearcher;
	private final F<ISourceChange, List<ASTNode>> addedStatementSearcher;
	
	
	@Inject
	public InlineMethodDetector(
			Logger logger, 
			@MethodDeclarationAnnotation String methodDeclarationLV,
			@StatementAnnotation String statementLV) {
		this.logger = logger;
		this.removedMethodSearcher = ChangeSearchUtils.searchFunc.flip().f(this.
			getBasicSearchCriteria(methodDeclarationLV, SourceChangeType.REMOVE)).
				andThen(ChangeSearchUtils.getLeafSourceChangeFunc().mapList()).
					andThen(ChangeSearchUtils.getNodeBeforeFunc.mapList());
		this.addedStatementSearcher = ChangeSearchUtils.searchFunc.flip().f(this.
			getBasicSearchCriteria(methodDeclarationLV, SourceChangeType.ADD)).
				andThen(ChangeSearchUtils.getLeafSourceChangeFunc().mapList()).
					andThen(ChangeSearchUtils.getNodeAfterFunc.mapList());
	}
	
	@Override
	public List<IDetectedRefactoring> f(ISourceChange change) {
		List<List<ASTNode>> removedMethods = this.removedMethodSearcher.f(change).
			group(rootRefEq);
		List<List<ASTNode>> addedStatements = this.addedStatementSearcher.f(change).
			group(rootRefEq);
		List<P2<List<ASTNode>, List<ASTNode>>> methodsStatementsPair = 
			removedMethods.bind(addedStatements, FJUtils.pairFunction((List
				<ASTNode>)null)).filter(rootNameEq.comap(FJUtils.getHeadFunc
					((ASTNode)null)).eq().tuple());
		Buffer<IDetectedRefactoring> allRefactorings = Buffer.empty();
		for(P2<List<ASTNode>, List<ASTNode>> pair : methodsStatementsPair.
				toCollection()) {
			List<ASTNode> methods = pair._1();
			List<List<ASTNode>> statementGroups = groupStatements(pair._2());
			allRefactorings.append(methods.bind(statementGroups, FJUtils.
				pairFunction((ASTNode) null, (List<ASTNode>)null)).filter(this.
					areStatementsExtractedFromMethod.tuple()).map(createInlineRefactoring.
						tuple()));
		}
		return allRefactorings.toList();
	}
	
	private List<List<ASTNode>> groupStatements(List<ASTNode> statements) {
		statements = statements.sort(Ord.intOrd.comap(ASTNode2IntegerUtils.
			getStart));
		List<List<ASTNode>> results = List.nil();
		if(statements.isEmpty()) return results;
		Buffer<ASTNode> currentBuffer = Buffer.empty();
		currentBuffer.snoc(statements.head());
		for(int i = 1; i < statements.length(); i ++) {
			ASTNode sta1 = statements.index(i - 1);
			ASTNode sta2 = statements.index(i);
			if(ASTNode2Boolean.areASTNodesNeighbors.f(sta1, sta2)) {
				currentBuffer.snoc(sta2);
			} else {
				results = results.snoc(currentBuffer.toList());
				currentBuffer = Buffer.empty();
				currentBuffer.snoc(sta2);
		}}
		results = results.snoc(currentBuffer.toList());
		return results;
	}
	
	private final F2<ASTNode, List<ASTNode>, Boolean> areStatementsExtractedFromMethod = 
		new F2<ASTNode, List<ASTNode>, Boolean>() {
		@Override
		public Boolean f(ASTNode method, List<ASTNode> statements) {
			List<ASTNode> methodStatements = ASTAnalyzer.getAllDecendantsFunc.
				f(method).filter(new F<ASTNode, Boolean>() {
				@Override
				public Boolean f(ASTNode node) {
					return node instanceof Statement;
			}});
			List<P2<ASTNode, ASTNode>> sameStatementPairs = FJUtils.getSamePairs
				(methodStatements, statements, Equal.equal(ASTAnalyzer.
					getASTNodeSameFunc().curry()));
			double perc = (double)sameStatementPairs.length()/(double)statements.
				length();
			return perc >= threshold;
	}}; 
	
	private final F2<ASTNode, List<ASTNode>, IDetectedRefactoring> 
		createInlineRefactoring = new F2<ASTNode, List<ASTNode>, IDetectedRefactoring>() {	
		@Override
		public IDetectedRefactoring f(ASTNode method, List<ASTNode> statements) {
			F<ASTNode, Boolean> filter = methodBindingEq.eq().f(method);
			List<ASTNode> invocations = getDownMethodInvocations.f(
				getUpMethodDeclaration.f(statements.head()).head()).filter(filter); 
			return new DetectedInlineMethodRefactoring(method, invocations);
	}};
}
