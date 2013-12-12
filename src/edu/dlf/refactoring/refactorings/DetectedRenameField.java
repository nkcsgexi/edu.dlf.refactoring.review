package edu.dlf.refactoring.refactorings;

import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.design.RefactoringType;
import fj.P;
import fj.P2;
import fj.data.List;

public class DetectedRenameField extends AbstractRefactoring{

	public static NodeListDescriptor SimpleNamesBefore = new NodeListDescriptor(){};
	public static NodeListDescriptor SimpleNamesAfter = new NodeListDescriptor(){};
	
	public DetectedRenameField(List<ASTNode> namesBefore, List<ASTNode> namesAfter) {
		super(RefactoringType.RenameField);
		this.addNodeList(SimpleNamesBefore, namesBefore);
		this.addNodeList(SimpleNamesAfter, namesAfter);
	}

	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.nil();
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.single(SimpleNamesBefore).snoc(SimpleNamesAfter);
	}

	@Override
	protected List<NodesDescriptor> getBeforeNodesDescriptor() {
		return List.single((NodesDescriptor)SimpleNamesBefore);
	}

	@Override
	protected List<NodesDescriptor> getAfterNodesDescriptor() {
		return List.single((NodesDescriptor)SimpleNamesAfter);
	}

	@Override
	protected List<P2<NodesDescriptor, TYPE>> getNodeTypesForCountingDelta() {
		return List.list(P.p((NodesDescriptor)SimpleNamesBefore, TYPE.CHANGE));
	}
	
	

}
