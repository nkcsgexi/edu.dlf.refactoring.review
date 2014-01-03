package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class InlineMethodDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final String compilationUnitLV;
	private final F<ISourceChange, List<ASTNode>> removedMethodSearcher;
	private final F<ISourceChange, List<ASTNode>> addedStatementSearcher;
	private final F<ASTNode, List<ASTNode>> getMethodInvocations = ASTAnalyzer.
		getDecendantFunc().f(ASTNode.METHOD_INVOCATION);
	private final Equal<ASTNode> methodBindingEq = Equal.stringEqual.comap
		(ASTNode2StringUtils.resolveBindingKey);
	private final Equal<ASTNode> rootRefEq = FJUtils.getReferenceEq
		((ASTNode)null).comap(ASTNode2ASTNodeUtils.getRootFunc);
	private final Equal<ASTNode> rootNameEq = Equal.stringEqual.comap(
		ASTNode2ASTNodeUtils.getRootFunc.andThen(ASTNode2StringUtils.
			getCompilationUnitName));
	
	@Inject
	public InlineMethodDetector(
			Logger logger, 
			@CompilationUnitAnnotation String compilationUnitLV,
			@MethodDeclarationAnnotation String methodDeclarationLV,
			@StatementAnnotation String statementLV) {
		this.logger = logger;
		this.compilationUnitLV = compilationUnitLV;
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
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
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
					areStatementsExtractedFromMethod.tuple()).map(createRefactoring.
						tuple()));
		}
		return allRefactorings.toList();
	}
	
	private List<List<ASTNode>> groupStatements(List<ASTNode> statements) {
		List<List<ASTNode>> results = List.nil();
		for(int i = 0; i < statements.length() - 1; i ++) {
			ASTNode st1 = statements.index(i);
			ASTNode st2 = statements.index(i + 1);
			
		}
		
		
		
		return results;
	}
	
	private final F2<ASTNode, List<ASTNode>, Boolean> areStatementsExtractedFromMethod = 
		new F2<ASTNode, List<ASTNode>, Boolean>() {
		@Override
		public Boolean f(ASTNode method, List<ASTNode> statements) {
			return null;
	}}; 
	
	private final F2<ASTNode, List<ASTNode>, IDetectedRefactoring> 
		createRefactoring = new F2<ASTNode, List<ASTNode>, IDetectedRefactoring>() {	
		@Override
		public IDetectedRefactoring f(ASTNode method, List<ASTNode> statements) {
			return null;
	}};


}
