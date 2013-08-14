package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IASTNodePair.ASTNodePair;
import edu.dlf.refactoring.utils.XList;

public interface IASTNodeMapStrategy {
	XList<ASTNodePair> map(XList<ASTNode> beforeNodes, XList<ASTNode> afterNodes) throws Exception;
}
