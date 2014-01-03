package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.StatementAnnotation;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import fj.Equal;
import fj.F;
import fj.data.List;

public class InlineMethodDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final F<ISourceChange, List<ASTNode>> removedMethodSearcher;
	private final F<ISourceChange, List<ASTNode>> addedStatementSearcher;
	private final F<ASTNode, List<ASTNode>> getMethodInvocations = ASTAnalyzer.
		getDecendantFunc().f(ASTNode.METHOD_INVOCATION);
	private final Equal<ASTNode> methodBindingEq = Equal.stringEqual.comap
		(ASTNode2StringUtils.resolveBindingKey);
	
	@Inject
	public InlineMethodDetector(Logger logger, 
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
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		//ASTAnalyzer.areNodesNeighbors(node1, node2)
		
		return null;
	}

}
