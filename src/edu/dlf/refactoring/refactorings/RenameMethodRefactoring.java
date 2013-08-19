package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.RefactoringType;
import fj.data.List;


public class RenameMethodRefactoring extends AbstractRefactoring{

	public RenameMethodRefactoring(List<ASTNode> namesBefore, List<ASTNode> namesAfter) {
		super(RefactoringType.RenameMethod);	
	}
}
