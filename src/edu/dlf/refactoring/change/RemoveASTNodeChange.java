package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.AbstractSourceChange;

public class RemoveASTNodeChange extends AbstractSourceChange{
		
	public RemoveASTNodeChange(String changeLevel, ASTNode node)
	{
		super(changeLevel, node, null);
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
		return SourceChangeType.REMOVE;
	}

}
