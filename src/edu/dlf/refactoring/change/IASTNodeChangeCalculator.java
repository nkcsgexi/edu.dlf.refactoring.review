package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public interface IASTNodeChangeCalculator {

	ISourceChange CalculateASTNodeChange(ASTNodePair pair);
}
