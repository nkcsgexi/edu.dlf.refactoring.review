package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ISourceChange;

public class RemoveASTNodeChange implements ISourceChange{
	
	private final ASTNode node;
	private final String changeLevel;
	
	public RemoveASTNodeChange(String changeLevel, ASTNode node)
	{
		this.changeLevel = changeLevel;
		this.node = node;
	}
	
	public ASTNode getRemovedNode()
	{
		return node;
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
	public String getSourceChangeLevel() {
		return this.changeLevel;
	}

}
