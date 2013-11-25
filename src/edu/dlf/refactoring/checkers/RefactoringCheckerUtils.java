package edu.dlf.refactoring.checkers;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.design.IDetectedRefactoring.SingleNodeDescriptor;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class RefactoringCheckerUtils {

	private RefactoringCheckerUtils() throws Exception
	{
		throw new Exception();
	}
	
	
	public static List<P2<Boolean, String>> performChecking(List<F2<ASTNode, ASTNode, P2<Boolean, 
		String>>> checkers, final ASTNode nodeBefore, final ASTNode nodeAfter)
	{
		return checkers.map(new F<F2<ASTNode,ASTNode,P2<Boolean,String>>, 
				P2<Boolean,String>>() {
					@Override
					public P2<Boolean, String> f(F2<ASTNode, ASTNode,
							P2<Boolean, String>> checker) {
						return checker.f(nodeBefore, nodeAfter);
					}
		}).filter(new F<P2<Boolean,String>, Boolean>() {
			@Override
			public Boolean f(P2<Boolean, String> p) {
				return !p._1();
			}
		});
	}
	
	
	public static List<ASTNode> getAllEffectedNodes(final IDetectedRefactoring 
		refactoring)
	{
		return refactoring.getNodeListDescritors().bind(new F<NodeListDescriptor, 
				List<ASTNode>>(){
			@Override
			public List<ASTNode> f(NodeListDescriptor d) {
				return refactoring.getEffectedNodeList(d);
			}}).append(refactoring.getSingleNodeDescriptors().map(new 
					F<SingleNodeDescriptor, ASTNode>(){
				@Override
				public ASTNode f(SingleNodeDescriptor d) {
					return refactoring.getEffectedNode(d);
				}}));
	}
}



