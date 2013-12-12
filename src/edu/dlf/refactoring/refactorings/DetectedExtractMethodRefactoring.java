package edu.dlf.refactoring.refactorings;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.P;
import fj.P2;
import fj.data.List;

public class DetectedExtractMethodRefactoring extends AbstractRefactoring{
	
	public static SingleNodeDescriptor DeclaredMethod = new SingleNodeDescriptor(){};
	public static NodeListDescriptor ExtractedStatements = new NodeListDescriptor(){};
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public DetectedExtractMethodRefactoring(List<ASTNode> statements, ASTNode method)
	{
		super(RefactoringType.ExtractMethod);
		this.addNodeList(ExtractedStatements, statements);
		this.addSingleNode(DeclaredMethod, method);
		logger.info("Extract method created.");
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Extracted statements:\n\n");
		List<ASTNode> statements = this.getEffectedNodeList(ExtractedStatements);
		for(ASTNode s : statements)
			sb.append(s + "\n");
		sb.append("Declared method:\n\n");
		sb.append(this.getEffectedNode(DeclaredMethod));
		return sb.toString();
	}

	@Override
	public List<SingleNodeDescriptor> getSingleNodeDescriptors() {
		return List.single(DeclaredMethod);
	}

	@Override
	public List<NodeListDescriptor> getNodeListDescritors() {
		return List.single(ExtractedStatements);
	}

	@Override
	protected List<NodesDescriptor> getBeforeNodesDescriptor() {
		return List.single((NodesDescriptor)ExtractedStatements);
	}

	@Override
	protected List<NodesDescriptor> getAfterNodesDescriptor() {
		return List.single((NodesDescriptor)DeclaredMethod);
	}

	@Override
	protected List<P2<NodesDescriptor, TYPE>> getNodeTypesForCountingDelta() {
		return List.list(
			P.p((NodesDescriptor)DeclaredMethod, TYPE.INSERT),
			P.p((NodesDescriptor)ExtractedStatements, TYPE.DELETE));
	}
	
}
