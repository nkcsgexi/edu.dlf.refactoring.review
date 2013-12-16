package edu.dlf.refactoring.design;


import java.util.Collection;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import com.google.common.base.Function;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.utils.XList;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;

public class ASTNodePair implements IASTNodePair{

	private final ASTNode nodeAfter;
	private final ASTNode nodeBefore;

	public ASTNodePair(ASTNode nodeBefore, ASTNode nodeAfter)
	{
		this.nodeBefore = nodeBefore;
		this.nodeAfter = nodeAfter;
	}

	public final static F<ASTNodePair, P2<ASTNode, ASTNode>> splitPairFunc =
		new F<ASTNodePair, P2<ASTNode,ASTNode>>() {
			@Override
			public P2<ASTNode, ASTNode> f(ASTNodePair pair) {
				return P.p(pair.getNodeBefore(), pair.getNodeAfter());
	}};
	
	public final static F2<ASTNode, ASTNode, ASTNodePair> createPairFunc = 
		new F2<ASTNode, ASTNode, ASTNodePair>() {
			@Override
			public ASTNodePair f(ASTNode n1, ASTNode n2) {
				return new ASTNodePair(n1, n2);
	}};
	
	
	public final static F2<ASTNodePair, StructuralPropertyDescriptor, 
		List<ASTNodePair>> getSubASTNodePairsFunc = 
			new F2<ASTNodePair, StructuralPropertyDescriptor, List<ASTNodePair>>() {
			@Override
			public List<ASTNodePair> f(ASTNodePair pair, 
				StructuralPropertyDescriptor descriptor) {
				return pair.selectNodePairByChildrenDescriptor(descriptor);
	}};  
		

	@Override
	public ASTNode getNodeBefore() {
		return nodeBefore;
	}
	
	@Override
	public ASTNode getNodeAfter() {
		return nodeAfter;
	}
	
	public ASTNodePair select(Function<ASTNode, ASTNode> func)
	{
		return new ASTNodePair(func.apply(nodeBefore), 
				func.apply(nodeAfter));
	}
	
	public List<ASTNodePair> selectNodePairByChildrenDescriptor(final 
		StructuralPropertyDescriptor descriptor) {
		P2<List<ASTNode>, List<ASTNode>> lists = getSubASTNodeByDescriptor
			(descriptor);
		List<ASTNode> beforeList = lists._1();
		List<ASTNode> afterList = lists._2();
		List<ASTNode> shorterList = beforeList.length() > afterList.length() 
			? afterList : beforeList;
		Boolean beforeIsShorter = shorterList == beforeList;
		int count = Math.abs(beforeList.length() - afterList.length());
		for(int i = 0 ; i < count; i ++)
			shorterList = shorterList.snoc(null);
		if(beforeIsShorter)
			return shorterList.zip(afterList).map(createPairFunc.tuple());
		else
			return beforeList.zip(shorterList).map(createPairFunc.tuple());
	}
	
	public P2<List<ASTNode>, List<ASTNode>> getSubASTNodeByDescriptor(final 
		StructuralPropertyDescriptor descriptor) {
		final F<ASTNode, List<ASTNode>> selector = ASTNode2ASTNodeUtils.
				getStructuralPropertyFunc.flip().f(descriptor);
		List<ASTNode> beforeList = selector.f(nodeBefore);
		List<ASTNode> afterList = selector.f(nodeAfter);
		return P.p(beforeList, afterList);
	}
	
	
	public ASTNodePair selectByPropertyDescriptor(final StructuralPropertyDescriptor 
			descriptor) {
		return select(new Function<ASTNode,ASTNode>(){
			@Override
			public ASTNode apply(ASTNode node){
				return (ASTNode) node.getStructuralProperty(descriptor);
			}});
	}
	
	public String getCompareInfor(F<ASTNode, String> converter)
	{
		return converter.f(this.getNodeBefore()) + "=>"+ converter.f(this.getNodeAfter());
	}
	
	public XList[] selectChildrenByDescriptor(final StructuralPropertyDescriptor descriptor)
	{
		XList<ASTNode> beforeList = new XList<ASTNode>((java.util.List)this.getNodeBefore().
				getStructuralProperty(descriptor));
		XList<ASTNode> afterList = new XList<ASTNode>((java.util.List)this.getNodeAfter().
				getStructuralProperty(descriptor));
		return new XList[]{beforeList, afterList};
	}

	public boolean areASTNodesSame() {
		return ASTAnalyzer.areASTNodesSame(nodeBefore, nodeAfter);
	}
	
	public boolean areASTNodeTypesSame()
	{
		return nodeBefore.getNodeType() == nodeAfter.getNodeType();
	}
}