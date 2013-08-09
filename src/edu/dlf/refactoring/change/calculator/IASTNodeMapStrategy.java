package edu.dlf.refactoring.change.calculator;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.utils.XList;

public interface IASTNodeMapStrategy {
	XList<ASTNodePair> map(XList<ASTNode> beforeNodes, XList<ASTNode> afterNodes) throws Exception;
}
