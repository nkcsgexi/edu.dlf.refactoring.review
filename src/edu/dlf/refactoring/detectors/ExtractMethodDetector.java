package edu.dlf.refactoring.detectors;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.ASTNodeEq;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class ExtractMethodDetector extends AbstractRefactoringDetector {

	private final Logger logger;
	private final F<ISourceChange, List<ASTNode>> getRemovedStatementsFunc;
	private final F<ISourceChange, List<ASTNode>> getAddedMethodsFunc;
	private final Equal<ASTNode> ancestorMethodGrouper = FJUtils.getReferenceEq
		((ASTNode)null).comap(ASTAnalyzer.getAncestorsFunc.f(ASTNode.
			METHOD_DECLARATION).andThen(getASTNodeListHead));
	private final Equal<ASTNode> ancestorTypeNameEqual = Equal.stringEqual.comap(
		ASTAnalyzer.getAncestorsFunc.f(ASTNode.TYPE_DECLARATION).andThen(
			getASTNodeListHead).andThen(ASTNode2StringUtils.
				getTypeDeclarationNameFunc));
	private final static double threshold = 0.5;
	
	@Inject
	public ExtractMethodDetector(
			Logger logger,
			@StatementAnnotation String statementLV,
			@ExpressionAnnotation String expChangeLevel,
			@MethodDeclarationAnnotation String methodDeclarationLV) {
		this.logger = logger;
		this.getRemovedStatementsFunc = ChangeSearchUtils.searchFunc.flip().
			f(getBasicSearchCriteria(statementLV, SourceChangeType.REMOVE)).
				andThen(SourceChangeUtils.getLeafSourceChangeFunc().mapList()).
					andThen(SourceChangeUtils.getNodeBeforeFunc().mapList());
		this.getAddedMethodsFunc = ChangeSearchUtils.searchFunc.flip().f
			(getBasicSearchCriteria(methodDeclarationLV, SourceChangeType.ADD)).andThen
				(SourceChangeUtils.getLeafSourceChangeFunc().mapList()).andThen
					(SourceChangeUtils.getNodeAfterFunc().mapList());
	}
		
	@Override
	public List<IDetectedRefactoring> f(ISourceChange change) {
		List<ASTNode> addedMethods = this.getAddedMethodsFunc.f(change);
		List<List<ASTNode>> removedStatementGroups = this.getRemovedStatementsFunc.
			f(change).group(ancestorMethodGrouper);
		List<P2<ASTNode, List<ASTNode>>> multiplied = addedMethods.bind(
			removedStatementGroups, FJUtils.pairFunction((ASTNode)null, 
				(List<ASTNode>)null));
		Buffer<IDetectedRefactoring> detectedRefactorings = Buffer.empty();
		for(P2<ASTNode, List<ASTNode>> pair : multiplied) {
			ASTNode method = pair._1();
			List<ASTNode> statements = pair._2();
			if(this.ancestorTypeNameEqual.eq(method, statements.head())) {
				List<ASTNode> statementsInMethod = ASTAnalyzer.getAllDecendantsFunc.
					f(method).filter(ASTNode2Boolean.isStatement);
				List<ASTNode> extractedStatements = FJUtils.getSamePairs(statements, 
					statementsInMethod, ASTNodeEq.astNodeContentEq).map(FJUtils.
						getFirstElementInPFunc((ASTNode)null, (ASTNode)null));
				int sameStatementsCount = extractedStatements.length();
				if((double)sameStatementsCount/(double)statementsInMethod.length() 
						>= threshold) {
					detectedRefactorings.snoc(new DetectedExtractMethodRefactoring
						(extractedStatements, method));
			}}
		}		
		return detectedRefactorings.toList();
	}
	

}
