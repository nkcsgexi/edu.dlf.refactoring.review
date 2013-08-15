package edu.dlf.refactoring.refactorings;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IRefactoring;
import edu.dlf.refactoring.utils.XList;

public class ExtractMethodRefactoring implements IRefactoring{
	
	public static SingleNodeDescriptor DeclaredMethod = new SingleNodeDescriptor(){};
	public static NodeListDescriptor ExtractedStatements = new NodeListDescriptor(){};
	
	@Override
	public ASTNode getEffectedNode(SingleNodeDescriptor descriptor) {
		if(descriptor == DeclaredMethod)
		{
			
		}
		return null;
	}
	
	@Override
	public XList<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor) {
		if(descriptor == ExtractedStatements)
		{
			
		}
		return null;
	}
	
	
	
}
