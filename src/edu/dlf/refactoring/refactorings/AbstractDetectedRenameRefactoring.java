package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.design.RefactoringType;
import fj.F;

public abstract class AbstractDetectedRenameRefactoring extends AbstractRefactoring{
	
	private final F<ASTNode, Boolean> isSimpleName;

	protected AbstractDetectedRenameRefactoring(RefactoringType refactoringType) {
		super(refactoringType);
		this.isSimpleName = ASTNode2Boolean.isASTNodeTypeRight.flip().f(ASTNode.
			SIMPLE_NAME);
	}
	
	public String getNameBefore() {
		return this.getEffectedNodesBefore().find(isSimpleName).some().toString();
	}
	
	public String getNameAfter() {
		return this.getEffectedNodesAfter().find(isSimpleName).some().toString();
	}

}
