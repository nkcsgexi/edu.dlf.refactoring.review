package edu.dlf.refactoring.change;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.utils.XList;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.data.List;
import fj.data.List.Buffer;

public class SourceChangeUtils {

	private static final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private SourceChangeUtils() throws Exception
	{	
		throw new Exception();
	}
	
	public static F<ISourceChange, ISourceChange> getPruneSourceChangeFunc()
	{
		return new F<ISourceChange, ISourceChange>() {
			@Override
			public ISourceChange f(ISourceChange change) {
				return pruneSourceChange(change);
			}};
	}
	
	
	public static F2<ASTNode, ASTNode, ISourceChange> getChangeCalculationFunc
		(final IASTNodeChangeCalculator calculator) {
		return new F2<ASTNode, ASTNode, ISourceChange>() {
			@Override
			public ISourceChange f(ASTNode nodeBefore, ASTNode nodeAfter) {
				return calculator.CalculateASTNodeChange(new ASTNodePair(nodeBefore, 
					nodeAfter));
	}};}
	

	public static F<ISourceChange, String> getChangeLVFunc = 
		new F<ISourceChange, String>() {
		@Override
		public String f(ISourceChange change) {
			return change.getSourceChangeLevel();
	}};
	
	
	public static ISourceChange pruneSourceChange(ISourceChange change)
	{
		boolean isNull = !pruneSubChanges(change);
		if(isNull)
			return null;
		return change;
	}
	
	
	public static List<ISourceChange> getSelfAndDescendent(ISourceChange root)
	{	
		Buffer<ISourceChange> buffer = Buffer.empty();
		XList<ISourceChange> unhandled = XList.CreateList();
		unhandled.add(root);
		while(unhandled.any())
		{
			ISourceChange first = unhandled.remove(0);
			buffer.snoc(first);
			unhandled.addAll(first.getSubSourceChanges());
		}
		return buffer.toList();
	}
	
	public static List<ISourceChange> calculateASTNodeChanges(final List<ASTNodePair> 
		pairs, final IASTNodeChangeCalculator calculator)
	{
		return pairs.map(new F<ASTNodePair, ISourceChange>() {
			@Override
			public ISourceChange f(ASTNodePair pair) {
				return calculator.CalculateASTNodeChange(pair);
			}
		});
	}

	public static List<ISourceChange> getAncestors(ISourceChange child)
	{
		Buffer<ISourceChange> results = Buffer.empty();
		for(ISourceChange change = child.getParentChange(); change != null; 
				change = change.getParentChange())
		{
			results.snoc(change);
		}
		return results.toList();
	}
	
	public static F2<ASTNode, ASTNode, ISourceChange> getChangeCalculator(
		final IASTNodeChangeCalculator calculator)
	{
		return new F2<ASTNode, ASTNode, ISourceChange>() {
			@Override
			public ISourceChange f(ASTNode n0, ASTNode n1) {
				return calculator.CalculateASTNodeChange(new ASTNodePair(n0, n1));
		}};
	}
	

	public static List<ISourceChange> getChildren(ISourceChange parent)
	{
		Buffer<ISourceChange> buffer = Buffer.empty();
		for(ISourceChange sub : parent.getSubSourceChanges())
		{
			buffer.snoc(sub);
		}
		return buffer.toList();
	}
	
	public static List<ASTNode> getEffectedASTNodes(ISourceChange change)
	{
		return getEffectedASTNodesAfter(change).append
			(getEffectedASTNodesBefore(change)).nub(Equal.equal(new F<ASTNode, 
				F<ASTNode,Boolean>>() {
				@Override
				public F<ASTNode, Boolean> f(final ASTNode node1) {
					return new F<ASTNode, Boolean>() {
						@Override
						public Boolean f(ASTNode node2) {
							return node1 == node2;
						}
					};
				}
			}));
	}
	
	public static F<IChangeSearchResult, ISourceChange> getLeafSourceChangeFunc()
	{
		return new F<IChangeSearchResult, ISourceChange>() {
			@Override
			public ISourceChange f(IChangeSearchResult result) {
				return result.getSourceChanges().last();
			}
		};
	}
	
	
	public static List<ASTNode> getEffectedASTNodesBefore(ISourceChange change)
	{
		return getAncestors(change).append(getChildren(change)).snoc(change).bind
			(new F<ISourceChange, List<ASTNode>>() {
				@Override
				public List<ASTNode> f(ISourceChange c) {
					return (List<ASTNode>) (c.getNodeBefore() != null ? 
						List.single(c.getNodeBefore()) : List.nil());
				}
			});
	}
	
	public static List<ASTNode> getEffectedASTNodesAfter(ISourceChange change)
	{
		return getAncestors(change).append(getChildren(change)).snoc(change).bind
			(new F<ISourceChange, List<ASTNode>>() {
				@Override
				public List<ASTNode> f(ISourceChange c) {
					return (List<ASTNode>) (c.getNodeAfter() != null ? 
						List.single(c.getNodeAfter()) : List.nil());
				}
			});
	}
	
	
	public static F<ISourceChange, ASTNode> getNodeBeforeFunc()
	{
		return new F<ISourceChange, ASTNode>() {
			@Override
			public ASTNode f(ISourceChange change) {
				return change.getNodeBefore();
			}
		};
	}
	
	public static F<ISourceChange, ASTNode> getNodeAfterFunc()
	{
		return new F<ISourceChange, ASTNode>(){
			@Override
			public ASTNode f(ISourceChange change) {
				return change.getNodeAfter();
			}};
	}
	
	
	private static boolean pruneSubChanges(ISourceChange change)
	{
		if(change.getSourceChangeType() == SourceChangeType.NULL)
			return false;
		if(change.getSourceChangeType() == SourceChangeType.PARENT)
		{
			List<ISourceChange> uselessChildren = List.nil();
			ISourceChange[] children = (((SubChangeContainer) change).
				getSubSourceChanges());
			for(ISourceChange kid : children) {
				if(pruneSubChanges(kid) == false) {
					uselessChildren = uselessChildren.snoc(kid);
				}
			}	
			((SubChangeContainer) change).removeSubChanges(uselessChildren.
				toCollection());
			return change.hasSubChanges();
		}
		else 
			return true;
	}
	
	public static String printChangeTree(ISourceChange change)
	{		
		return internalPrintChangeTree(change).foldLeft(
			new F2<StringBuilder, String, StringBuilder>() {
				@Override
				public StringBuilder f(StringBuilder sb, String s) {
					return sb.append(s + System.lineSeparator());
				}
		}, new StringBuilder()).toString();
	}

	private static List<String> internalPrintChangeTree(ISourceChange change) 
	{
		try{
			if(change.getSourceChangeType() == SourceChangeType.UNKNOWN) {
				logger.fatal("Unkown node:");
				logger.fatal(change.getNodeBefore());
				logger.fatal(change.getNodeAfter());
			}
			if(change.getSourceChangeType() == SourceChangeType.PARENT)
			{
				SubChangeContainer container = (SubChangeContainer) change;
				List<ISourceChange> children = FJUtils.createListFromArray(container.
					getSubSourceChanges());
				List<String> subLines = children.bind(new F<ISourceChange, 
					List<String>>(){
					@Override
					public List<String> f(ISourceChange c) {
						return internalPrintChangeTree(c);
					}});
				List<String> lines = subLines.map(new F<String, String>(){
					@Override
					public String f(String s) {
						return '\t' + s;
					}});	
				return lines.cons(change.getSourceChangeLevel());
			}
			if(change.getSourceChangeType() == SourceChangeType.NULL)
				return List.nil();
			return List.single(change.getSourceChangeLevel() + ":" + change.
				getSourceChangeType().toString());
		}catch(Exception e)
		{
			Logger logger = ServiceLocator.ResolveType(Logger.class);
			logger.fatal(e);
			return List.nil();
		}
	}
}













