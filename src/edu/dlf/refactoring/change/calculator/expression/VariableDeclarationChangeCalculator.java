package edu.dlf.refactoring.change.calculator.expression;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.IASTNodeMapStrategy;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class VariableDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator fragCalculator;
	private final IASTNodeChangeCalculator typeCalculator;
	private final ChangeBuilder changeBuilder;
	
	@Inject
	public VariableDeclarationChangeCalculator(
			@TypeAnnotation IASTNodeChangeCalculator typeCalculator, 
			@VariableDeclarationFragmentAnnotation IASTNodeChangeCalculator fragCalculator,
			@VariableDeclarationAnnotation String changeLevel)
	{
		this.typeCalculator = typeCalculator;
		this.fragCalculator = fragCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ISourceChange simpleChange = changeBuilder.buildSimpleChange(pair);
		if(simpleChange != null)
			return simpleChange;
		try{
			SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
			VariableDeclarationExpression vdBefore = (VariableDeclarationExpression) pair.getNodeBefore();
			VariableDeclarationExpression vdAfter = (VariableDeclarationExpression) pair.getNodeAfter();
			
			ASTNode typeB = (ASTNode) vdBefore.getStructuralProperty(VariableDeclarationExpression.TYPE_PROPERTY);
			ASTNode typeA = (ASTNode) vdAfter.getStructuralProperty(VariableDeclarationExpression.TYPE_PROPERTY);
			container.addSubChange(this.typeCalculator.CalculateASTNodeChange(new ASTNodePair(typeB, typeA)));
			
			XList<ASTNode> fragB = new XList<ASTNode>((List)vdBefore.getStructuralProperty
					(VariableDeclarationExpression.FRAGMENTS_PROPERTY));
			XList<ASTNode> fragA =  new XList<ASTNode>((List)vdAfter.getStructuralProperty
					(VariableDeclarationExpression.FRAGMENTS_PROPERTY));
			IASTNodeMapStrategy mapper = new SimilarityASTNodeMapStrategy(new IDistanceCalculator(){
				@Override
				public int calculateDistance(ASTNode before, ASTNode after) {
					return XStringUtils.distance(
						before.getStructuralProperty(VariableDeclarationFragment.NAME_PROPERTY).toString(),
						after.getStructuralProperty(VariableDeclarationFragment.NAME_PROPERTY).toString());
			}});
			
			container.addMultiSubChanges(mapper.map(fragB, fragA).select(new Function<ASTNodePair, 
				ISourceChange>(){
					@Override
					public ISourceChange apply(ASTNodePair p) {
						return fragCalculator.CalculateASTNodeChange(p);
					}}));
			return container;
		}catch(Exception e)
		{
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
		
	}
	
	
	

}
