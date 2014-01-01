package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.IJavaElementPair;

public final class SubChangeContainer extends AbstractSourceChange{
	
	public SubChangeContainer(String changeLevel, IASTNodePair pair) {
		super(changeLevel, pair);
	}
	
	public SubChangeContainer(String changeLevel, IJavaElementPair pair) {
		super(changeLevel, pair);
	}

	public SourceChangeType getSourceChangeType() {
		return SourceChangeType.PARENT;
	}
	
}
