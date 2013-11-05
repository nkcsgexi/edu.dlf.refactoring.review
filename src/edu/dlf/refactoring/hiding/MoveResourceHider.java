package edu.dlf.refactoring.hiding;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2IntegerUtils;
import edu.dlf.refactoring.analyzers.ASTNodeEq;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.refactorings.DetectedMoveRefactoring;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.data.List;
import fj.data.Option;

public class MoveResourceHider extends AbstractRefactoringHider{

	private final Logger logger;
	private final HashMap<Integer, F<ASTNode, List<ASTNode>>> finders;
	private final HashMap<Integer, Equal<ASTNode>> filters;
	private final F<ASTNode, List<ASTNode>> getTypeAncestors;
	private final F<ASTNode, List<ASTNode>> getTypeDecendants;
	
	@Inject
	public MoveResourceHider(Logger logger) {
		this.logger = logger;
		this.getTypeAncestors = ASTAnalyzer.getAncestorsFunc.f(ASTNode.TYPE_DECLARATION);
		this.getTypeDecendants = ASTAnalyzer.getDecendantFunc().f(ASTNode.TYPE_DECLARATION);
		this.finders = new HashMap<Integer, F<ASTNode, List<ASTNode>>>();
		this.finders.put(ASTNode.METHOD_DECLARATION, ASTAnalyzer.getDecendantFunc().
			f(ASTNode.METHOD_DECLARATION));
		this.filters = new HashMap<Integer, Equal<ASTNode>>();
		this.filters.put(ASTNode.METHOD_DECLARATION, ASTNodeEq.MethodDeclarationNameEq);
	}
		
	@Override
	public ASTNode f(IDetectedRefactoring refactoring, ASTNode root) {
		ASTNode addedDec = refactoring.getEffectedNode(DetectedMoveRefactoring.
			AddedDeclarationDescripter);
		ASTNode removedDec = refactoring.getEffectedNode(DetectedMoveRefactoring.
			RemovedDeclarationDescriptor);
		ASTUpdator updator = new ASTUpdator();
		if(ASTAnalyzer.sameMainTypeEq.eq(addedDec, root))
		{
			findaddedDec(addedDec, root);
		}
		if(ASTAnalyzer.sameMainTypeEq.eq(removedDec, root))
		{
			findRemovedDecPosition(removedDec, root);
		}
		return root;
	}

	

	private Option<ASTNode> findaddedDec(final ASTNode add, final ASTNode root) {
		final F<ASTNode, List<ASTNode>> finder = this.finders.get(add.getNodeType());
		final Equal<ASTNode> filter = this.filters.get(add.getNodeType());
		return finder.f(root).find(new F<ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode node) {
				return filter.eq(add, node);
		}});
	}
	
	private int findRemovedDecPosition(ASTNode dec, ASTNode root) {
		final Ord<ASTNode> sorter = Ord.intOrd.comap(ASTNode2IntegerUtils.getLength);
		final List<ASTNode> typeDecs = getTypeAncestors.f(dec);
		final List<ASTNode> typeDecsInRoot = getTypeDecendants.f(root);
		ASTNode type = typeDecs.minus(ASTNodeEq.TypeDeclarationNameEq, typeDecsInRoot).sort
			(sorter).head();
		ASTNode typeInRoot = typeDecsInRoot.minus(ASTNodeEq.TypeDeclarationNameEq, 
			typeDecs).sort(sorter).head();
		List<ASTNode> neighbors = ASTAnalyzer.getDecendantFunc().f(dec.getNodeType()).f(type);
		int index = neighbors.elementIndex(ASTNodeEq.ReferenceEq, dec).some();
		List<ASTNode> decsInRoot = finders.get(dec.getNodeType()).f(typeInRoot);
		if(index == 0)
			return decsInRoot.head().getStartPosition();
		if(index > decsInRoot.length())
			return ASTNode2IntegerUtils.getEnd.f(decsInRoot.last());
		return ASTNode2IntegerUtils.getEnd.f(decsInRoot.index(index - 1));
	}
	
	
	
	
	
	
	
}
