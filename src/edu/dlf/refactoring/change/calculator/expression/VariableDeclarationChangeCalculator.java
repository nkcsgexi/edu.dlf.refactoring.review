package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class VariableDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		VariableDeclarationExpression vdBefore = (VariableDeclarationExpression) pair.getNodeBefore();
		VariableDeclarationExpression vdAfter = (VariableDeclarationExpression) pair.getNodeAfter();
		
		ASTNode typeB = (ASTNode) vdBefore.getStructuralProperty(VariableDeclarationExpression.TYPE_PROPERTY);
		ASTNode typeA = (ASTNode) vdAfter.getStructuralProperty(VariableDeclarationExpression.TYPE_PROPERTY);
		
		ASTNode[] fragB =  (ASTNode[]) vdBefore.getStructuralProperty(VariableDeclarationExpression.
				FRAGMENTS_PROPERTY);
		ASTNode[] fragA =  (ASTNode[]) vdAfter.getStructuralProperty(VariableDeclarationExpression.
				FRAGMENTS_PROPERTY);
		return null;
	}

}
