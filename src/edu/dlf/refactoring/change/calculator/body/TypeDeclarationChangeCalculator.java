package edu.dlf.refactoring.change.calculator.body;


import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.ASTNodeMapperUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class TypeDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final IASTNodeChangeCalculator mChangeCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snChangeCalculator;
	private final IASTNodeChangeCalculator fCalculator;
	private final IASTNodeChangeCalculator typeCalculator;
	
	@Inject
	public TypeDeclarationChangeCalculator(
		Logger logger,
		@SimpleNameAnnotation IASTNodeChangeCalculator snChangeCalculator,
		@TypeDeclarationAnnotation String changeLevel,
		@TypeAnnotation IASTNodeChangeCalculator typeCalculator,
		@FieldDeclarationAnnotation IASTNodeChangeCalculator fCalculator,
		@MethodDeclarationAnnotation IASTNodeChangeCalculator mChangeCalculator)
	{
		this.logger = logger;
		this.snChangeCalculator = snChangeCalculator;
		this.mChangeCalculator = mChangeCalculator;
		this.fCalculator = fCalculator;
		this.typeCalculator = typeCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}

	
	private F<ASTNode, String> getTypeFunc = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			return node.getStructuralProperty(TypeDeclaration.NAME_PROPERTY).toString();
	}};

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		logger.debug("Comparing types: " + getTypeFunc.f(pair.getNodeBefore()) + 
			"=>" + getTypeFunc.f(pair.getNodeAfter()));
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		try{
			SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
			container.addSubChange(snChangeCalculator.CalculateASTNodeChange(pair.select
				(new Function<ASTNode, ASTNode>(){
				@Override
				public ASTNode apply(ASTNode n) {
					return (ASTNode) n.getStructuralProperty(TypeDeclaration.NAME_PROPERTY);
				}})));	
			TypeDeclaration typeB = (TypeDeclaration) pair.getNodeBefore();
			TypeDeclaration typeA = (TypeDeclaration) pair.getNodeAfter();
			container.addMultiSubChanges(calculateSuperTypeChanges(typeB, typeA));
			container.addMultiSubChanges(calculateFieldChanges(typeB, typeA));
			container.addMultiSubChanges(calculateMethodChanges(typeB, typeA));
			return container;
		}catch (Exception e) {
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}
		
	private final F<ASTNode, List<ASTNode>> getSuperTypes = new F<ASTNode, List<ASTNode>>() {
		@Override
		public List<ASTNode> f(ASTNode type) {
			List<ASTNode> inter = ASTNode2ASTNodeUtils.getStructuralPropertyFunc.f(type, 
				TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY);
			List<ASTNode> cla = ASTNode2ASTNodeUtils.getStructuralPropertyFunc.f(type, 
				TypeDeclaration.SUPERCLASS_TYPE_PROPERTY);
			return inter.append(cla);
	}};
		
	
	private Collection<ISourceChange> calculateSuperTypeChanges(TypeDeclaration 
		typeB, TypeDeclaration typeA) {
		final F<P2<ASTNode, ASTNode>, ISourceChange> calculateChange = 
			SourceChangeUtils.getChangeCalculationFunc(typeCalculator).tuple();
		final F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> 
			superTypeMapper = ASTNodeMapperUtils.getASTNodeMapper(4, ASTNodeMapperUtils.
				getASTNodeSimilarityFunc(10));
		return superTypeMapper.f(getSuperTypes.f(typeB), getSuperTypes.f(typeA)).map
			(calculateChange).toCollection();
	}

	
	
	private Collection<ISourceChange> calculateFieldChanges(TypeDeclaration typeB, 
			TypeDeclaration typeA) {
		List<ASTNode> fieldsBefore = FJUtils.createListFromArray
			((ASTNode[])typeB.getFields());
		List<ASTNode> fieldsAfter = FJUtils.createListFromArray
			((ASTNode[])typeA.getFields());
		F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> mapper = 
			ASTNodeMapperUtils.getASTNodeMapper(Integer.MIN_VALUE, ASTNodeMapperUtils.
				getDefaultASTNodeSimilarityFunc());
		return mapper.f(fieldsBefore, fieldsAfter).map(new F<P2<ASTNode,ASTNode>, 
			ISourceChange>() {
				@Override
				public ISourceChange f(P2<ASTNode, ASTNode> p) {
					return fCalculator.CalculateASTNodeChange(new ASTNodePair
						(p._1(), p._2()));
		}}).toCollection();
	}
	
	private final F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> 
		methodNameCamelCaseMapper = ASTNodeMapperUtils.getASTNodeMapper(70,
			ASTNodeMapperUtils.getCommonWordsASTNodeSimilarityScoreFunc(100, 
				ASTNode2StringUtils.getMethodNameFunc));	
				
	private Collection<ISourceChange> calculateMethodChanges(TypeDeclaration typeB,
		TypeDeclaration typeA) {
		final List<ASTNode> methodsBefore = FJUtils.createListFromArray
			((ASTNode[])typeB.getMethods());
		final List<ASTNode> methodsAfter = FJUtils.createListFromArray
			((ASTNode[])typeA.getMethods());
		List<ISourceChange> changes = methodNameCamelCaseMapper.f(methodsBefore, 
			methodsAfter).map(new F<P2<ASTNode, ASTNode>, ISourceChange>(){
			@Override
			public ISourceChange f(P2<ASTNode, ASTNode> pair) {
				logger.debug(ASTNode2StringUtils.getMethodNameFunc.f(pair._1()) 
					+ "->" + ASTNode2StringUtils.getMethodNameFunc.f(pair._2()));
				return mChangeCalculator.CalculateASTNodeChange(new ASTNodePair
					(pair._1(), pair._2()));
			}});
		return changes.toCollection();
	}

}
