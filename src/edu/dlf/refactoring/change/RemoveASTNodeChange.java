package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.JavaElementPair;

public final class RemoveASTNodeChange extends AbstractSourceChange{
		
	public RemoveASTNodeChange(String changeLevel, ASTNode node){
		super(changeLevel, new ASTNodePair(node, null));
	}
	
	public RemoveASTNodeChange(String changeLevel, IJavaElement element) {
		super(changeLevel, new JavaElementPair(element, null));
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.REMOVE;
	}

}
