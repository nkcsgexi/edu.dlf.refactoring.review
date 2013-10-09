package edu.dlf.refactoring.change.calculator;


import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class TypeDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator mChangeCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snChangeCalculator;
	private final IASTNodeChangeCalculator fCalculator;
	
	@Inject
	public TypeDeclarationChangeCalculator(
			@SimpleNameAnnotation IASTNodeChangeCalculator snChangeCalculator,
			@TypeDeclarationAnnotation String changeLevel,
			@FieldDeclarationAnnotation IASTNodeChangeCalculator fCalculator,
			@MethodDeclarationAnnotation IASTNodeChangeCalculator mChangeCalculator)
	{
		this.snChangeCalculator = snChangeCalculator;
		this.mChangeCalculator = mChangeCalculator;
		this.fCalculator = fCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}


	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
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
			
			container.addMultiSubChanges(calculateFieldChanges(typeB, typeA));
			
			IASTNodeMapStrategy strategy = new SimilarityASTNodeMapStrategy(new IDistanceCalculator(){
				@Override
				public int calculateDistance(ASTNode before, ASTNode after) {
					Name nb = (Name) before.getStructuralProperty(MethodDeclaration.NAME_PROPERTY);
					Name na = (Name) after.getStructuralProperty(MethodDeclaration.NAME_PROPERTY);
					return XStringUtils.distance(nb.toString(), na.toString());
				}});
			
			container.addMultiSubChanges(strategy.map(new XList<ASTNode>(typeB.getMethods()), 
				new XList<ASTNode>(typeA.getMethods())).select(new Function<ASTNodePair, ISourceChange>(){
					@Override
					public ISourceChange apply(ASTNodePair pair) {
						return mChangeCalculator.CalculateASTNodeChange(pair);
					}}));
			return container;
		}catch (Exception e)
		{
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}


	private Collection<ISourceChange> calculateFieldChanges(TypeDeclaration typeB, 
			TypeDeclaration typeA) {
		List<ASTNode> fieldsBefore = FunctionalJavaUtil.createListFromArray
			((ASTNode[])typeB.getFields());
		List<ASTNode> fieldsAfter = FunctionalJavaUtil.createListFromArray
			((ASTNode[])typeA.getFields());
		F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> mapper = 
			ASTAnalyzer.getASTNodeMapper(ASTAnalyzer.
				getASTNodeDefaultSimilarityScoreCalculator());
		return mapper.f(fieldsBefore, fieldsAfter).map(new F<P2<ASTNode,ASTNode>, 
			ISourceChange>() {
				@Override
				public ISourceChange f(P2<ASTNode, ASTNode> p) {
					return fCalculator.CalculateASTNodeChange(new ASTNodePair
						(p._1(), p._2()));
				}
		}).toCollection();
	}

}
