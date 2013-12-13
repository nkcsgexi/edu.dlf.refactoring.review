package edu.dlf.refactoring.refactorings;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.analyzers.ASTNode2IntegerUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.RefactoringType;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;

abstract class AbstractRefactoring implements IDetectedRefactoring
{
	protected abstract List<NodesDescriptor> getBeforeNodesDescriptor();
	protected abstract List<NodesDescriptor> getAfterNodesDescriptor();
	protected abstract List<P2<NodesDescriptor, TYPE>> getNodeTypesForCountingDelta();
	
	private final HashMap<SingleNodeDescriptor, ASTNode> singleNodes;
	private final HashMap<NodeListDescriptor, List<ASTNode>> nodeLists;
	private final RefactoringType refactoringType;
	
	
	protected AbstractRefactoring(RefactoringType refactoringType)
	{
		this.refactoringType = refactoringType;
		this.singleNodes = new HashMap<SingleNodeDescriptor, ASTNode>();
		this.nodeLists = new HashMap<NodeListDescriptor, List<ASTNode>>();
	}
	
	protected void addSingleNode(SingleNodeDescriptor descriptor, ASTNode node)
	{
		this.singleNodes.put(descriptor, node);
	}
	
	protected void addNodeList(NodeListDescriptor decriptor, List<ASTNode> list)
	{
		this.nodeLists.put(decriptor, list);
	}
	
	private final F<NodesDescriptor, List<ASTNode>> getNodes = 
		new F<NodesDescriptor, List<ASTNode>>() {
		@Override
		public List<ASTNode> f(NodesDescriptor descriptor) {
			if(descriptor instanceof SingleNodeDescriptor)
				return List.single(getEffectedNode((SingleNodeDescriptor)descriptor));
			else 
				return getEffectedNodeList((NodeListDescriptor)descriptor);
	}}; 
	
	private final F2<Integer, Integer, Integer> adder = 
		new F2<Integer, Integer, Integer>() {
		@Override
		public Integer f(Integer i0, Integer i1) {
			return i0 + i1;
	}};
	
	private final F<List<Integer>, Integer> addAll = new F<List<Integer>, 
		Integer>() {
		@Override
		public Integer f(List<Integer> list) {
			return list.foldLeft(adder, 0);
	}};
	
	@Override
	public List<P2<TYPE, Integer>> getDeltaSummary() {
		final List<NodesDescriptor> descriptors = getNodeTypesForCountingDelta().
			map(FJUtils.getFirstElementInPFunc((NodesDescriptor)null, (TYPE)null));
		final List<TYPE> deltaTypes = getNodeTypesForCountingDelta().map(FJUtils.
			getSecondElementInPFunc((NodesDescriptor)null, (TYPE)null));
		final List<Integer> lineSpans = descriptors.map(getNodes).map
			(ASTNode2IntegerUtils.getLinesSpan.mapList().andThen(addAll));
		final Equal<P2<TYPE, Integer>> grouper = Equal.equal(
			new F2<P2<TYPE, Integer>, P2<TYPE, Integer>, Boolean>() {
				@Override
				public Boolean f(P2<TYPE, Integer> p0, P2<TYPE, Integer> p1) {
					return p0._1() == p1._1();
		}}.curry());
		return deltaTypes.zip(lineSpans).group(grouper).map(
			new F<List<P2<TYPE,Integer>>, P2<TYPE, Integer>>() {
				@Override
				public P2<TYPE, Integer> f(List<P2<TYPE, Integer>> group) {
					F<P2<TYPE, Integer>, Integer> getInt = FJUtils.
						getSecondElementInPFunc((TYPE) null, (Integer)null);
					return P.p(group.head()._1(), addAll.f(group.map(getInt)));
		}});
	}
	
	@Override
	public List<ASTNode> getEffectedNodesBefore() {
		return getBeforeNodesDescriptor().bind(getNodes);
	}
	

	@Override
	public List<ASTNode> getEffectedNodesAfter() {
		return getAfterNodesDescriptor().bind(getNodes);
	}
	
	@Override
	public ASTNode getEffectedNode(SingleNodeDescriptor descriptor) {
		return this.singleNodes.get(descriptor);
	}
	
	@Override
	public List<ASTNode> getEffectedNodeList(NodeListDescriptor descriptor) {
		return this.nodeLists.get(descriptor);
	}
	
	@Override
	public RefactoringType getRefactoringType() {
		return this.refactoringType;
	}
}