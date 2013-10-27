package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
import fj.data.List;


public class RenameTypeRefactoring extends AbstractRefactoring{

	public static NodeListDescriptor SimpleNamesBefore = new NodeListDescriptor(){};
	public static NodeListDescriptor SimpleNamesAfter = new NodeListDescriptor(){};
	
	
	public RenameTypeRefactoring(List<ASTNode> namesBefore, List<ASTNode> 
		namesAfter) {
		super(RefactoringType.RenameType);
		this.addNodeList(SimpleNamesBefore, namesBefore);
		this.addNodeList(SimpleNamesAfter, namesAfter);
	}
	
	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.nil();
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.single(SimpleNamesBefore).snoc(SimpleNamesAfter);
	}


}
