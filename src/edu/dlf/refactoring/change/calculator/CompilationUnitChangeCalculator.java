package edu.dlf.refactoring.change.calculator;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class CompilationUnitChangeCalculator implements IJavaModelChangeCalculator, 
	IASTNodeChangeCalculator{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator typeChangeCalculator;
	private final ChangeBuilder changeBuilder;

	@Inject
	public CompilationUnitChangeCalculator(
			@CompilationUnitAnnotation String changeLevel,
			@TypeDeclarationAnnotation IASTNodeChangeCalculator typeChangeCalculator)
	{
		this.typeChangeCalculator = typeChangeCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		
		ASTNode cuBefore = ASTAnalyzer.parseICompilationUnit(pair.GetBeforeElement());
		ASTNode cuAfter = ASTAnalyzer.parseICompilationUnit(pair.GetAfterElement());
		return CalculateASTNodeChange(new ASTNodePair(cuBefore, cuAfter));
	}


	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		ASTNode cuBefore = pair.getNodeBefore();
		ASTNode cuAfter = pair.getNodeAfter();
		ASTNodePair aPair = new ASTNodePair(cuBefore, cuAfter);
		
		ISourceChange change = changeBuilder.buildSimpleChange(aPair);
		if(change != null)
			return change;
		try{	
			
			SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
			List typesBefore =  (List) cuBefore.getStructuralProperty(CompilationUnit.TYPES_PROPERTY);
			List typesAfter = (List) cuAfter.getStructuralProperty(CompilationUnit.TYPES_PROPERTY);
			
			IASTNodeMapStrategy mapper = new SimilarityASTNodeMapStrategy(new IDistanceCalculator(){
				@Override
				public int calculateDistance(ASTNode before, ASTNode after) {
					String nb = before.getStructuralProperty(TypeDeclaration.NAME_PROPERTY).toString();
					String na = after.getStructuralProperty(TypeDeclaration.NAME_PROPERTY).toString();
					return XStringUtils.distance(nb, na);
				}});
			
			container.addMultiSubChanges(mapper.map(new XList<ASTNode>(typesBefore), 
					new XList<ASTNode>(typesAfter)).
				select(new Function<ASTNodePair, ISourceChange>(){
					@Override
					public ISourceChange apply(ASTNodePair p) {
						return typeChangeCalculator.CalculateASTNodeChange(p);
					}}));
			return container;
		} catch(Exception e)
		{
			logger.fatal(e);
			return changeBuilder.createUnknownChange(aPair);
		}
	}

}
