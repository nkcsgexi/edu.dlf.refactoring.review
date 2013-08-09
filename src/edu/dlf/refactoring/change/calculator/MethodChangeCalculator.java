package edu.dlf.refactoring.change.calculator;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class MethodChangeCalculator implements IASTNodeChangeCalculator {

	Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		MethodDeclaration methodBefore = (MethodDeclaration) pair.getNodeBefore();
		MethodDeclaration methodAfter = (MethodDeclaration) pair.getNodeBefore();
		
		try {
			compareBody(methodBefore.getBody(), methodAfter.getBody());
		} catch (Exception e) {
			logger.fatal(e);
		}
		
		return null;
	}

	private void compareBody(Block b1, Block b2) throws Exception
	{
		
	}
	

	

}
