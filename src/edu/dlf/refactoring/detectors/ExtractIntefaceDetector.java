package edu.dlf.refactoring.detectors;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.refactorings.DetectedExtractSuperTypeRefactoring;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;

public class ExtractIntefaceDetector extends AbstractRefactoringDetector{

	private final Logger logger;
	private final IChangeSearchCriteria addCUCriteria;
	private final IChangeSearchCriteria addTypeCriteria;
	private final IChangeSearchCriteria addSuperTypeCriteria;

	private final F<ASTNode, List<ASTNode>> getTypeFunc = ASTAnalyzer.getDecendantFunc().
			f(ASTNode.TYPE_DECLARATION);
	
	private final F<ASTNode, String> resolveTypeDec = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode typeDeclaration) {
			return ((TypeDeclaration)typeDeclaration).resolveBinding().getKey();
	}};
	
	private final F<ASTNode, String> resolveTypeRef = new F<ASTNode, String>(){
		@Override
		public String f(ASTNode type) {
			return ((Type)type).resolveBinding().getKey();
	}};
	
	private final F<P2<ASTNode, ASTNode>, Boolean> isDecRefSame = 
		new F<P2<ASTNode,ASTNode>, Boolean>() {
		@Override
		public Boolean f(P2<ASTNode, ASTNode> pair) {
			return resolveTypeDec.f(pair._1()).equals(resolveTypeRef.f(pair._2()));
	}};
	
	private final F<P2<ASTNode, ASTNode>, String> getKeyOfPair = 
		new F<P2<ASTNode, ASTNode>, String>() {
		@Override
		public String f(P2<ASTNode, ASTNode> pair) {
			return resolveTypeDec.f(pair._1());
	}};
	
	private final Ord<P2<ASTNode, ASTNode>> order = Ord.stringOrd.comap(getKeyOfPair);
	private final Equal<P2<ASTNode, ASTNode>> grouper = Equal.stringEqual.comap(getKeyOfPair);
	
	@Inject
	public ExtractIntefaceDetector(
			Logger logger,
			@TypeDeclarationAnnotation String tdChange,
			@CompilationUnitAnnotation String cuChange,
			@TypeAnnotation String tChange)
	{
		this.logger = logger;
		ChangeCriteriaBuilder builder = new ChangeCriteriaBuilder();
		this.addTypeCriteria = builder.addSingleChangeCriteria(tdChange, 
			SourceChangeType.ADD).getSearchCriteria();
		builder.reset();
		this.addCUCriteria = builder.addSingleChangeCriteria(cuChange, 
			SourceChangeType.ADD).getSearchCriteria();
		builder.reset();
		this.addSuperTypeCriteria = builder.addSingleChangeCriteria(tdChange, 
			SourceChangeType.PARENT).addSingleChangeCriteria(tChange, 
				SourceChangeType.ADD).getSearchCriteria();	
	}
	
	@Override
	public List<IDetectedRefactoring> detectRefactoring(ISourceChange change) {		
		List<ASTNode> addedTypeDeclarations = addTypeCriteria.search(change).map(
			SourceChangeUtils.getLeafSourceChangeFunc()).map(SourceChangeUtils.
				getNodeAfterFunc()).append(addCUCriteria.search(change).map(
					SourceChangeUtils.getLeafSourceChangeFunc()).map(SourceChangeUtils.
						getNodeAfterFunc()).bind(getTypeFunc));
		List<ASTNode> addedSuperTypes = addSuperTypeCriteria.search(change).map
			(SourceChangeUtils.getLeafSourceChangeFunc()).map(SourceChangeUtils.
				getNodeAfterFunc());
		List<P2<ASTNode, ASTNode>> funcRefPairs = addedTypeDeclarations.bind
			(addedSuperTypes, ASTAnalyzer.getPNodeFunc()).filter(isDecRefSame);
		return funcRefPairs.sort(order).group(grouper).map(new F<List<P2<ASTNode,ASTNode>>, 
			IDetectedRefactoring>() {
			@Override
			public IDetectedRefactoring f(List<P2<ASTNode, ASTNode>> list) {
				return new DetectedExtractSuperTypeRefactoring(list.head()._1(),
					list.map(ASTAnalyzer.getP2Func));
			}});
	}

}
