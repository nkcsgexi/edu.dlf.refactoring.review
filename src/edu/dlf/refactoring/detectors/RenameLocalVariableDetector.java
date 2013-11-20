package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.DetectedRenameLocalVariable;
import fj.Equal;
import fj.F;
import fj.P2;
import fj.data.List;

public class RenameLocalVariableDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final IChangeSearchCriteria searchCriteria;

	@Inject
	public RenameLocalVariableDetector(
			Logger logger,
			ChangeCriteriaBuilder builder,
			@MethodDeclarationAnnotation String methodDecLevel,
			@VariableDeclarationFragmentAnnotation String variableDecLevel,
			@SimpleNameAnnotation String simpleNameLevel) {
		this.logger = logger;
		this.searchCriteria = builder.addSingleChangeCriteria(methodDecLevel, 
			SourceChangeType.PARENT).addNonEmptyGap().addSingleChangeCriteria
				(variableDecLevel, SourceChangeType.PARENT).addSingleChangeCriteria
					(simpleNameLevel, SourceChangeType.UPDATE).getSearchCriteria();
	}
	
	private final F<ASTNode, List<ASTNode>> getNamesWithSameBindingKey
		= new F<ASTNode, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(ASTNode name) {
				ASTNode root = name.getRoot();
				Equal<ASTNode> eq = Equal.stringEqual.comap(ASTNode2StringUtils.
					resolveBindingKey);
				return ASTAnalyzer.getDecendantFunc().f(ASTNode.SIMPLE_NAME, root).
					filter(eq.eq(name));
	}};
	
	private final F<P2<List<ASTNode>, List<ASTNode>>, IDetectedRefactoring> 
		createDetectedRefactoring = new F<P2<List<ASTNode>,List<ASTNode>>, 
			IDetectedRefactoring>() {
			@Override
			public IDetectedRefactoring f(P2<List<ASTNode>, List<ASTNode>> pair) {
				logger.info("Rename local variable detected: " + pair._1().head().
					toString() + "=>" + pair._2().head().toString());
				return new DetectedRenameLocalVariable(pair._1(), pair._2());
	}};
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {
		List<IChangeSearchResult> results = this.searchCriteria.search(change);
		List<ASTNode> namesBefore = results.map(ChangeSearchUtils.
			getLeafSourceChangeFunc()).map(ChangeSearchUtils.getNodeBeforeFunc);
		List<ASTNode> namesAfter = results.map(ChangeSearchUtils.
			getLeafSourceChangeFunc()).map(ChangeSearchUtils.getNodeAfterFunc);
		return namesBefore.zip(namesAfter).map(FunctionalJavaUtil.extendMapper2Product
			(getNamesWithSameBindingKey)).map(createDetectedRefactoring);
	}

}
