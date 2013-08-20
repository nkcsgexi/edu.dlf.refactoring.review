package edu.dlf.refactoring.refactorings;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.data.List;


public class RenameMethodRefactoring extends AbstractRefactoring{

	public static NodeListDescriptor SimpleNamesBefore = new NodeListDescriptor(){};
	public static NodeListDescriptor SimpleNamesAfter = new NodeListDescriptor(){};
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public RenameMethodRefactoring(List<ASTNode> namesBefore, List<ASTNode> namesAfter) {
		super(RefactoringType.RenameMethod);
		this.addNodeList(SimpleNamesBefore, namesBefore);
		this.addNodeList(SimpleNamesAfter, namesAfter);
		this.logger.info("Rename method created."); ;
	}
	
	public String toString()
	{
		return "";
	}
}
