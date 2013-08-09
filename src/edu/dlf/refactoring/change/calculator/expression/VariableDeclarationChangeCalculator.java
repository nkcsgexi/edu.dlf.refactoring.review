package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ASTAnnotations.TypeAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.VariableDeclarationAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class VariableDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator fragCalculator;
	private final IASTNodeChangeCalculator typeCalculator;
	private final ChangeBuilder changeBuilder;
	
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
			
		VariableDeclarationExpression vdBefore = (VariableDeclarationExpression) pair.getNodeBefore();
		VariableDeclarationExpression vdAfter = (VariableDeclarationExpression) pair.getNodeAfter();
		
		ASTNode typeB = (ASTNode) vdBefore.getStructuralProperty(VariableDeclarationExpression.TYPE_PROPERTY);
		ASTNode typeA = (ASTNode) vdAfter.getStructuralProperty(VariableDeclarationExpression.TYPE_PROPERTY);
		
		XList<ASTNode> fragB =  new XList<ASTNode>((ASTNode[]) vdBefore.getStructuralProperty(VariableDeclarationExpression.
				FRAGMENTS_PROPERTY));
		XList<ASTNode> fragA =  new XList<ASTNode>((ASTNode[]) vdAfter.getStructuralProperty(VariableDeclarationExpression.
				FRAGMENTS_PROPERTY));
		
		XList<ASTNode> changedFragB = fragB.Except(fragA, ASTAnalyzer.getASTEqualityComparer());
		XList<ASTNode> changedFragA = fragA.Except(fragB, ASTAnalyzer.getASTEqualityComparer());
		
		
		
		return null;
	}
	
	
	

}
