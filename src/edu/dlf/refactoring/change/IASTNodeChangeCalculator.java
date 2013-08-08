package edu.dlf.refactoring.change;

import edu.dlf.refactoring.design.ASTNodePair;

public interface IASTNodeChangeCalculator {

	Void CalculateASTNodeChange(ASTNodePair pair);
}
