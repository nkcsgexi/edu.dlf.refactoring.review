package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ASTAnnotations.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class TypeDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final IASTNodeChangeCalculator mChangeCalculator;
	private final ChangeBuilder changeBuilder;
	
	@Inject
	public TypeDeclarationChangeCalculator(
			@TypeDeclarationAnnotation String changeLevel,
			@MethodDeclarationAnnotation IASTNodeChangeCalculator mChangeCalculator)
	{
		this.mChangeCalculator = mChangeCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}


	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		TypeDeclaration typeB = (TypeDeclaration) pair.getNodeBefore();
		TypeDeclaration typeA = (TypeDeclaration) pair.getNodeAfter();
		
		
		
		
		
		
		
		return null;
	}
	
	private XList<ASTNodePair> mapMethodNames(XList<MethodDeclaration> methodsBefore, XList<MethodDeclaration> methodsAfter)
	{
		XList<ASTNodePair> results = new XList<ASTNodePair>();
		
		
		
		
		return null;
	}

}
