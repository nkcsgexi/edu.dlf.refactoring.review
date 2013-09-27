package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.base.Function;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.utils.XList;

public class SequentialASTNodeMapStrategy implements IASTNodeMapStrategy{

	@Override
	public XList<ASTNodePair> map(XList<ASTNode> beforeNodes,
			XList<ASTNode> afterNodes) throws Exception {
		int commonLength = Math.min(beforeNodes.size(), afterNodes.size());
		XList<ASTNodePair> result = XList.CreateList();
		for(int i = 0; i < commonLength; i++)
		{
			result.add(new ASTNodePair(beforeNodes.get(i),afterNodes.get(i)));
		}
		
		if(beforeNodes.size() > commonLength)
		{
			result.addAll(beforeNodes.subList(commonLength, beforeNodes.size()).select(
				new Function<ASTNode, ASTNodePair>(){
					@Override
					public ASTNodePair apply(ASTNode node) {
						return new ASTNodePair(node, null);
					}}));
		}
		
		if(afterNodes.size() > commonLength)
		{
			result.addAll(afterNodes.subList(commonLength, afterNodes.size()).select(
				new Function<ASTNode, ASTNodePair>(){
					@Override
					public ASTNodePair apply(ASTNode node) {
						return new ASTNodePair(null, node);
					}}));
		}
		return result;
	}

}
