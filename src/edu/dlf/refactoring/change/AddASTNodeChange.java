package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.AbstractSourceChange;

public class AddASTNodeChange extends AbstractSourceChange{
	
	protected AddASTNodeChange(String changeLevel, ASTNode node)
	{
		super(changeLevel, null, node);
	}	
	
	protected AddASTNodeChange(String changeLevel, IJavaElement element)
	{
		super(changeLevel, null, element);
	}	

	@Override
	public boolean hasSubChanges() {
		return false;
	}

	@Override
	public ISourceChange[] getSubSourceChanges() {
		return new ISourceChange[0];
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.ADD;
	}
}
