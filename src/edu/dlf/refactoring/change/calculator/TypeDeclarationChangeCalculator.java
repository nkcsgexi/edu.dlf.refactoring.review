package edu.dlf.refactoring.change.calculator;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class TypeDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator mChangeCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snChangeCalculator;
	
	@Inject
	public TypeDeclarationChangeCalculator(
			@SimpleNameAnnotation IASTNodeChangeCalculator snChangeCalculator,
			@TypeDeclarationAnnotation String changeLevel,
			@MethodDeclarationAnnotation IASTNodeChangeCalculator mChangeCalculator)
	{
		this.snChangeCalculator = snChangeCalculator;
		this.mChangeCalculator = mChangeCalculator;
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

}
