package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.ChangeCriteriaBuilder;
import edu.dlf.refactoring.detectors.ChangeSearchUtils;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.refactorings.DetectedRenameMethodRefactoring;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;

public class RenameMethodChecker implements IRefactoringChecker{

	private final Logger logger;
	private final ChangeCriteriaBuilder builder;
	private final IChangeSearchCriteria criteria;

	@Inject
	public RenameMethodChecker(Logger logger, 
			ChangeCriteriaBuilder builder,
			@SimpleNameAnnotation String sNameLevel) {
		this.logger = logger;
		this.builder = builder;
		this.criteria = this.builder.addSingleChangeCriteria(sNameLevel, 
			SourceChangeType.UPDATE).getSearchCriteria();
	}
	private final Equal<ASTNode> eqFunc = Equal.stringEqual.comap(ASTAnalyzer.
			getContainingCompilationUnitName());
		
	private final Equal<List<ASTNode>> listEqFunc = Equal.stringEqual.comap
			(new F<List<ASTNode>, String>() {
			@Override
			public String f(List<ASTNode> l) {
				return ASTAnalyzer.getContainingCompilationUnitName().f(l.head());
	}});
		
	private final Ord<ASTNode> ordFunc = Ord.stringOrd.comap(ASTAnalyzer.
			getContainingCompilationUnitName());
	
	private final F<P2<List<ASTNode>, List<ASTNode>>, Boolean> filter = FunctionalJavaUtil.
		convertEqualToProduct(Equal.intEqual.comap(FunctionalJavaUtil.
			getListLengthFunc((ASTNode)null)));
		
	@Override
	public synchronized ICheckingResult checkRefactoring(IDetectedRefactoring 
		detected, IImplementedRefactoring implemented) {
		List<ASTNode> namesBefore = detected.getEffectedNodeList(
			DetectedRenameMethodRefactoring.SimpleNamesBefore);
		List<ASTNode> namesAfter = detected.getEffectedNodeList(
			DetectedRenameMethodRefactoring.SimpleNamesAfter);
		List<ISourceChange> nameChanges = implemented.getSourceChanges().
			bind(ChangeSearchUtils.searchFunc.flip().f(criteria).andThen(
				ChangeSearchUtils.getLeafSourceChangeFunc().mapList()));
		List<ASTNode> namesBeforeAutoRef = nameChanges.map(SourceChangeUtils.
			getNodeBeforeFunc());

		logger.debug("Auto rename method updates count:" + namesBeforeAutoRef.length());
		
		List<List<ASTNode>> groupedBeforeManual = namesBefore.sort(ordFunc).group(eqFunc);
		List<List<ASTNode>> groupedBeforeAuto = namesBeforeAutoRef.sort(ordFunc).group(eqFunc);
		
		List<List<ASTNode>> missedUpdates = groupedBeforeAuto.minus(listEqFunc, 
			groupedBeforeManual);
		List<List<ASTNode>> additionalUpdates = groupedBeforeManual.minus(listEqFunc, 
			groupedBeforeAuto);

		List<P2<List<ASTNode>, List<ASTNode>>> incorrectUpdates = FunctionalJavaUtil.
			pairEqualElements(groupedBeforeManual, groupedBeforeAuto, 
			listEqFunc).filter(filter);
		if(missedUpdates.isEmpty() && additionalUpdates.isEmpty() && 
			incorrectUpdates.isEmpty())
			return new DefaultCheckingResult(true, detected);
		else
			return new DefaultCheckingResult(false, detected);
	}
	
	

}
