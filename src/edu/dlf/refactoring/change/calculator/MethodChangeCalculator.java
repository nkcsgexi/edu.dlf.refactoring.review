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
		XList<ASTNode> nodesBefore = new XList(b1.statements()).cast(ASTNode.class);
		XList<ASTNode> nodesAfter = new XList(b2.statements()).cast(ASTNode.class);	
		XList<String> statementsBefore = nodesBefore.cast(String.class);
		XList<String> statementsAfter = nodesAfter.cast(String.class);
		List<Delta> diffs = DiffUtils.diff(statementsBefore, statementsAfter).getDeltas();
		for(Delta diff : diffs)
		{
			if(diff.getType() == TYPE.CHANGE)
			{
				
			}
			else if(diff.getType() == TYPE.DELETE)
			{

			 
			} else if(diff.getType() == TYPE.INSERT)
			{
		
			}
		}
	}
	

	private class MethodSourceChange implements ISourceChange
	{
		
		
	}
	

}
