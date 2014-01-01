package edu.dlf.refactoring.change;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ASTNodePair;

public class UnknownSourceChange extends AbstractSourceChange {

	public UnknownSourceChange(ASTNode nodeBefore, ASTNode nodeAfter) {
		super("UNKNOWN", new ASTNodePair( nodeBefore, nodeAfter));
	}

	@Override
	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.UNKNOWN;
	}
}
