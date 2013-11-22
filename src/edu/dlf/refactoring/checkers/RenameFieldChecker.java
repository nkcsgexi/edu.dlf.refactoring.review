package edu.dlf.refactoring.checkers;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
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
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.refactorings.DetectedRenameField;
import fj.Effect;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;

public class RenameFieldChecker implements IRefactoringChecker {

	private final Logger logger;
	private final IChangeSearchCriteria criteria;
	
	@Inject
	public RenameFieldChecker(Logger logger, ChangeCriteriaBuilder builder,
			@SimpleNameAnnotation String simpleNameLV) {
		this.logger = logger;
		this.criteria = builder.addSingleChangeCriteria(simpleNameLV, 
			SourceChangeType.UPDATE).getSearchCriteria();
	}
	
	private final F<ASTNode, String> getParentCUName = ASTNode2ASTNodeUtils.
		getRootFunc.andThen(ASTNode2StringUtils.getCompilationUnitName);
	
	private final Equal<ASTNode> grouper = Equal.stringEqual.comap(getParentCUName);
	
	private final Ord<List<ASTNode>> sorter = Ord.stringOrd.comap(FJUtils.
		getHeadFunc((ASTNode)null).andThen(ASTNode2StringUtils.
			getCompilationUnitName)); 
	
	private final Equal<List<ASTNode>> countChecker = Equal.intEqual.comap
		(FJUtils.getListLengthFunc((ASTNode)null));
	
	private final Equal<List<ASTNode>> cuNameChecker = Equal.stringEqual.comap(
		FJUtils.getHeadFunc((ASTNode)null).andThen(getParentCUName));
	
	private final Effect<List<ASTNode>> logChangeGroup = 
		new Effect<List<ASTNode>>() {
		@Override
		public void e(List<ASTNode> list) {
			String cuName = FJUtils.getHeadFunc((ASTNode)null).
				andThen(getParentCUName).f(list);
			logger.info(cuName + ":" + list.length()); 
	}}; 
	
	private final Effect<ISourceChange> logChange = new Effect<ISourceChange>() {
		@Override
		public void e(ISourceChange change) {
			String cuName = ASTNode2StringUtils.getCompilationUnitName.f
				(change.getNodeBefore());
			logger.info(cuName + "\n" + SourceChangeUtils.printChangeTree(change));
	}};
	
	@Override
	public ICheckingResult checkRefactoring(IDetectedRefactoring detected,
		IImplementedRefactoring implemented) {
		implemented.getSourceChanges().foreach(logChange);
		List<ASTNode> autoNamesBefore = implemented.getSourceChanges().map
			(ChangeSearchUtils.searchFunc.flip().f(criteria)).
			foldLeft(FJUtils.listAppender((IChangeSearchResult)null), 
				FJUtils.createEmptyList((IChangeSearchResult)null)).
					map(ChangeSearchUtils.getLeafSourceChangeFunc().andThen(
						ChangeSearchUtils.getNodeBeforeFunc));
		List<ASTNode> manualNamesBefore = detected.getEffectedNodeList(
			DetectedRenameField.SimpleNamesBefore);
		List<List<ASTNode>> autoGroups = autoNamesBefore.group(grouper).sort(sorter);
		List<List<ASTNode>> manGroups = manualNamesBefore.group(grouper).sort(sorter);
		logger.info("Auto:");
		autoGroups.foreach(logChangeGroup);
		logger.info("Manual:");
		manGroups.foreach(logChangeGroup);	
		if(autoGroups.length() == manGroups.length()) {
			List<P2<List<ASTNode>, List<ASTNode>>> zipped = autoGroups.zip(manGroups);
			if(zipped.forall(FJUtils.convertEqualToProduct(countChecker))) {
				if(zipped.forall(FJUtils.convertEqualToProduct(cuNameChecker))) {
					logger.info("Rename field is correct.");
					return new DefaultCheckingResult(true, detected);
				}
			}	
		}
		return new DefaultCheckingResult(false, detected);
	}

}
