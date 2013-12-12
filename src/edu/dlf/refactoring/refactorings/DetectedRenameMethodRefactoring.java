package edu.dlf.refactoring.refactorings;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.P;
import fj.P2;
import fj.data.List;


public class DetectedRenameMethodRefactoring extends AbstractRefactoring{

	public static NodeListDescriptor SimpleNamesBefore = new NodeListDescriptor(){};
	public static NodeListDescriptor SimpleNamesAfter = new NodeListDescriptor(){};
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public DetectedRenameMethodRefactoring(List<ASTNode> namesBefore, List<ASTNode> namesAfter) {
		super(RefactoringType.RenameMethod);
		this.addNodeList(SimpleNamesBefore, namesBefore);
		this.addNodeList(SimpleNamesAfter, namesAfter);
		this.logger.info("Rename method created."); ;
	}
	
	public String toString()
	{
		return "";
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
		return List.single(P.p((NodesDescriptor)SimpleNamesBefore, TYPE.CHANGE));
	}
}
