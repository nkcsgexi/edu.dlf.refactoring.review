package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class UnknownSourceChange extends ASTNodePair implements ISourceChange {

	public UnknownSourceChange(ASTNode nodeBefore, ASTNode nodeAfter) {
		super(nodeBefore, nodeAfter);
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
		return "UNKNOWN";
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.UNKNOWN;
	}

}
