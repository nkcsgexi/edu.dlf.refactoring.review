package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.JavaElementPair;

public final class AddASTNodeChange extends AbstractSourceChange{
	
	public AddASTNodeChange(String changeLevel, ASTNode node){
		super(changeLevel, new ASTNodePair(null, node));
	}
	
	public AddASTNodeChange(String changeLevel, IJavaElement element) {
		super(changeLevel, new JavaElementPair(null, element));
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.ADD;
	}
}
