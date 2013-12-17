package edu.dlf.refactoring.change.calculator;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2IntegerUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.ASTNodeMapperUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AnnotationTypeDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnumDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import fj.Equal;
import fj.F;
import fj.P2;
import fj.data.List;


public class CompilationUnitChangeCalculator implements IJavaModelChangeCalculator, 
	IASTNodeChangeCalculator{

	private final Logger logger;
	private final IASTNodeChangeCalculator typeChangeCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator enumDeclarationCal;
	private final IASTNodeChangeCalculator annotationTypeCal;	
	
	@Inject
	public CompilationUnitChangeCalculator(
			Logger logger,
			@CompilationUnitAnnotation String changeLevel,
			@TypeDeclarationAnnotation IASTNodeChangeCalculator typeChangeCalculator,
			@EnumDeclarationAnnotation IASTNodeChangeCalculator enumDeclarationCal,
			@AnnotationTypeDeclarationAnnotation IASTNodeChangeCalculator annotationTypeCal) {
		this.logger = logger;
		this.typeChangeCalculator = typeChangeCalculator;
		this.enumDeclarationCal = enumDeclarationCal;
		this.annotationTypeCal = annotationTypeCal;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	private final F<ASTNode, List<ASTNode>> getAbstractTypeFunc = 
		ASTNode2ASTNodeUtils.getStructuralPropertyFunc.flip().
			f(CompilationUnit.TYPES_PROPERTY);
	
	private final Equal<ASTNode> nodeTypeEq = Equal.intEqual.comap(ASTNode2IntegerUtils.
		getKind);
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		logger.debug("Calculate change: " + JavaModelAnalyzer.getElementNameFunc.
			f(pair.getElementBefore()) + "->" + JavaModelAnalyzer.getElementNameFunc.
				f(pair.getElementAfter()) );
		ASTNode cuBefore = ASTAnalyzer.parseICompilationUnit(pair.getElementBefore());
		ASTNode cuAfter = ASTAnalyzer.parseICompilationUnit(pair.getElementAfter());
		return CalculateASTNodeChange(new ASTNodePair(cuBefore, cuAfter));
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange simple = changeBuilder.buildSimpleChange(pair);
		if(simple != null)
			return simple;
		logger.debug("Calculate change: " + ASTNode2StringUtils.getCompilationUnitName.
			f(pair.getNodeBefore()) + "->" + ASTNode2StringUtils.getCompilationUnitName.
				f(pair.getNodeAfter()));
		ASTNode cuBefore = pair.getNodeBefore();
		ASTNode cuAfter = pair.getNodeAfter();
		ASTNodePair aPair = new ASTNodePair(cuBefore, cuAfter);
		
		ISourceChange change = changeBuilder.buildSimpleChange(aPair);
		if(change != null)
			return change;
		try{	
			SubChangeContainer container = changeBuilder.createSubchangeContainer
				(pair);
			List<ASTNode> typesBefore =  getAbstractTypeFunc.f(cuBefore);
			List<ASTNode> typesAfter = getAbstractTypeFunc.f(cuAfter);
			List<P2<List<ASTNode>, List<ASTNode>>> groups = FJUtils.
				pairEqualElements(typesBefore.group(nodeTypeEq), typesAfter.
				group(nodeTypeEq), Equal.intEqual.comap(FJUtils.getHeadFunc
					((ASTNode)null).andThen(ASTNode2IntegerUtils.getKind)));		
			F<P2<List<ASTNode>, List<ASTNode>>, List<ASTNodePair>> mapper = 
				ASTNodeMapperUtils.getASTNodeMapper(5, ASTNodeMapperUtils.
				getCommonWordsASTNodeSimilarityScoreFunc(10, new F<ASTNode, String>() {
				@Override
				public String f(ASTNode node) {
					return ((AbstractTypeDeclaration)node).getName().getIdentifier();
			}})).tuple().andThen(ASTNodePair.createPairFunc.tuple().mapList());
			Collection<ASTNodePair> nodePairs = groups.bind(mapper).toCollection();
			addSubChanges(container, nodePairs);
			return container;
		} catch(Exception e) {
			logger.fatal(e);
			return changeBuilder.createUnknownChange(aPair);
		}
	}

	private void addSubChanges(SubChangeContainer container, Collection
		<ASTNodePair> nodePairs) throws Exception {
		for(ASTNodePair typePair : nodePairs) {
			int kind = typePair.getNodeBefore() != null ? typePair.getNodeBefore().
				getNodeType() : typePair.getNodeAfter().getNodeType();
			switch(kind) {
			case ASTNode.TYPE_DECLARATION:
				container.addSubChange(this.typeChangeCalculator.
					CalculateASTNodeChange(typePair));
				continue;
			case ASTNode.ENUM_DECLARATION:	
				container.addSubChange(this.enumDeclarationCal.
					CalculateASTNodeChange(typePair));
				continue;
			case ASTNode.ANNOTATION_TYPE_DECLARATION:
				container.addSubChange(this.annotationTypeCal.
					CalculateASTNodeChange(typePair));
				continue;
			default: 
				throw new Exception("Unknown type declaration.");
			}
		}
	}
}
