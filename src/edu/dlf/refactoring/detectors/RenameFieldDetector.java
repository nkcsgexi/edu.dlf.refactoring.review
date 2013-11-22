package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.refactorings.DetectedRenameField;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.data.List;

public class RenameFieldDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final IChangeSearchCriteria criteria;
	private final String cuLV;

	@Inject
	public RenameFieldDetector(Logger logger, 
		ChangeCriteriaBuilder builder,
		@CompilationUnitAnnotation String cuLV,
		@FieldDeclarationAnnotation String fieldDecLV,
		@VariableDeclarationFragmentAnnotation String varDecFragLV,
		@SimpleNameAnnotation String simpleNameLV) {
		this.logger = logger;
		this.cuLV = cuLV;
		this.criteria = builder.addSingleChangeCriteria(fieldDecLV, SourceChangeType.PARENT).
			addSingleChangeCriteria(varDecFragLV, SourceChangeType.PARENT).
				addSingleChangeCriteria(simpleNameLV, SourceChangeType.UPDATE).
					getSearchCriteria();
	}
	
	private final F2<String, ASTNode, List<ASTNode>> getSimpleNamesFunc =
		new F2<String, ASTNode, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(String key, ASTNode root) {
				F<ASTNode, Boolean> filter = ASTNode2StringUtils.resolveBindingKey.
					andThen(Equal.stringEqual.eq(key));
				return ASTAnalyzer.getDecendantFunc().f(ASTNode.SIMPLE_NAME, root).
					filter(filter);
	}};
	
	private final F<ISourceChange, List<ISourceChange>> getCUChanges = 
		new F<ISourceChange, List<ISourceChange>>() {
			@Override
			public List<ISourceChange> f(ISourceChange root) {
				F<ISourceChange, Boolean> filter = SourceChangeUtils.getChangeLVFunc.
					andThen(Equal.stringEqual.eq(cuLV));
				return 	SourceChangeUtils.getSelfAndDescendent(root).filter(filter);
	}};
	
	private final F<ASTNode, Boolean> nodeNonNullFilter = FJUtils.
		nonNullFilter((ASTNode)null);
	
	private final F<ISourceChange, List<ASTNode>> getChangedCUBefore = getCUChanges.
		andThen(ChangeSearchUtils.getNodeBeforeFunc.mapList());
	
	private final F<ISourceChange, List<ASTNode>> getChangedCUAfter = getCUChanges.
		andThen(ChangeSearchUtils.getNodeAfterFunc.mapList());
	
	private final F2<F<ASTNode, List<ASTNode>>, List<ASTNode>, List<ASTNode>> 
		collectNodesFromNodes = new F2<F<ASTNode,List<ASTNode>>, List<ASTNode>, 
			List<ASTNode>>() {
			@Override
			public List<ASTNode> f(F<ASTNode, List<ASTNode>> mapper, List<ASTNode> roots) {
				return roots.map(mapper).foldLeft(FJUtils.listAppender
					((ASTNode)null), FJUtils.createEmptyList((ASTNode)null));
	}};
	
	private final F2<List<ASTNode>, List<ASTNode>, IDetectedRefactoring> 
		createRefactoring = new F2<List<ASTNode>, List<ASTNode>, IDetectedRefactoring>() {
			@Override
			public IDetectedRefactoring f(List<ASTNode> namesB, List<ASTNode> namesA) {
				logger.info("Rename field detected: " + namesB.head().toString() 
					+ "=>" + namesA.head().toString());
				return new DetectedRenameField(namesB, namesA);
	}};
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(final ISourceChange change) {
		List<ISourceChange> nameChanges = ChangeSearchUtils.searchFunc.f(change).andThen(
			ChangeSearchUtils.getLeafSourceChangeFunc().mapList()).f(criteria);
		logger.debug("Number of field name changes: " + nameChanges.length());
		List<ASTNode> decNamesBefore = nameChanges.map(ChangeSearchUtils.getNodeBeforeFunc);
		List<ASTNode> decNamesAfter = nameChanges.map(ChangeSearchUtils.getNodeAfterFunc);
		List<F<ASTNode, List<ASTNode>>> beforeFinders = decNamesBefore.map
			(ASTNode2StringUtils.resolveBindingKey.andThen(getSimpleNamesFunc.curry()));
		List<F<ASTNode, List<ASTNode>>> afterFinders = decNamesAfter.map
			(ASTNode2StringUtils.resolveBindingKey.andThen(getSimpleNamesFunc.curry()));
		List<ASTNode> cusBefore = getChangedCUBefore.f(change).filter(nodeNonNullFilter);
		List<ASTNode> cusAfter = getChangedCUAfter.f(change).filter(nodeNonNullFilter);
		List<List<ASTNode>> changeNamesBefore = beforeFinders.map
			(collectNodesFromNodes.flip().f(cusBefore));
		List<List<ASTNode>> changedNamesAfter = afterFinders.map
			(collectNodesFromNodes.flip().f(cusAfter));
		return changeNamesBefore.zip(changedNamesAfter).map(createRefactoring.tuple());
	}

}
