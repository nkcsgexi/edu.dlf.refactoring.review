package edu.dlf.refactoring.design;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import com.google.common.base.Function;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.utils.XList;

public interface IASTNodePair
{
	public ASTNode getNodeBefore();

	public ASTNode getNodeAfter();
	
	
}

