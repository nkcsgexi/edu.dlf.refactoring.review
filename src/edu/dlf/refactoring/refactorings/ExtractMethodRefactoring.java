package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.utils.XList;

public class ExtractMethodRefactoring extends AbstractRefactoring{
	
	public static SingleNodeDescriptor DeclaredMethod = new SingleNodeDescriptor(){};
	public static NodeListDescriptor ExtractedStatements = new NodeListDescriptor(){};
	
	public ExtractMethodRefactoring(XList<ASTNode> statements, ASTNode method)
	{
		super(RefactoringType.ExtractMethod);
		this.addNodeList(ExtractedStatements, statements);
		this.addSingleNode(DeclaredMethod, method);
	}
}
