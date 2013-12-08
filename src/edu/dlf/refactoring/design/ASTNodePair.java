package edu.dlf.refactoring.design;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import com.google.common.base.Function;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.utils.XList;
import fj.F;
import fj.P;
import fj.P2;

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
	
	
	public ASTNodePair selectByPropertyDescriptor(final StructuralPropertyDescriptor 
			descriptor)
	{
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
		XList<ASTNode> beforeList = new XList<ASTNode>((List)this.getNodeBefore().
				getStructuralProperty(descriptor));
		XList<ASTNode> afterList = new XList<ASTNode>((List)this.getNodeAfter().
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