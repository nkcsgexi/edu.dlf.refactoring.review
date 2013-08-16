package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
import fj.data.List;

public class ExtractMethodRefactoring extends AbstractRefactoring{
	
	public static SingleNodeDescriptor DeclaredMethod = new SingleNodeDescriptor(){};
	public static NodeListDescriptor ExtractedStatements = new NodeListDescriptor(){};
	
	public ExtractMethodRefactoring(List<ASTNode> statements, ASTNode method)
	{
		super(RefactoringType.ExtractMethod);
		this.addNodeList(ExtractedStatements, statements);
		this.addSingleNode(DeclaredMethod, method);
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Extracted statements:\n\n");
		List<ASTNode> statements = this.getEffectedNodeList(ExtractedStatements);
		for(ASTNode s : statements)
			sb.append(s + "\n");
		sb.append("Declared method:\n\n");
		sb.append(this.getEffectedNode(DeclaredMethod));
		return sb.toString();
	}
	
}
