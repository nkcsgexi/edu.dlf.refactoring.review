package edu.dlf.refactoring.change;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.base.Function;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;
import edu.dlf.refactoring.utils.XList.IAggregator;
import fj.Equal;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class SourceChangeUtils {

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
			}
		};
	}
	
	public static ISourceChange pruneSourceChange(ISourceChange change)
	{
		pruneSubChanges(change);
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
	
	
	
	private static boolean pruneSubChanges(ISourceChange change)
	{
		if(change.getSourceChangeType() == SourceChangeType.NULL)
			return false;
		if(change.getSourceChangeType() == SourceChangeType.PARENT)
		{
			XList<ISourceChange> uselessChildren = new XList<ISourceChange>();
			XList<ISourceChange> children = new XList<ISourceChange>
				(((SubChangeContainer) change).getSubSourceChanges());
			for(ISourceChange kid : children)
			{
				if(pruneSubChanges(kid) == false)
				{
					uselessChildren.add(kid);
				}
			}
			
			if(uselessChildren.size() == children.size())
			{
				return false;
			}
			else
			{
				((SubChangeContainer) change).removeSubChanges(uselessChildren);
				return true;
			}
		}
		return true;
	}
	
	public static String printChangeTree(ISourceChange change)
	{		
		return internalPrintChangeTree(change).aggregate(new IAggregator<String, String>(){
			@Override
			public String aggregate(String s1, String s2) {
				return s1 + System.lineSeparator() + s2;
			}});
	}

	private static XList<String> internalPrintChangeTree(ISourceChange change) 
	{
		try{
			if(change.getSourceChangeType() == SourceChangeType.PARENT)
			{
				SubChangeContainer container = (SubChangeContainer) change;
				XList<ISourceChange> children = new XList<ISourceChange>(container.
					getSubSourceChanges());
				XList<String> subLines = children.selectMany(new Function<ISourceChange, 
						Collection<String>>(){
					@Override
					public Collection<String> apply(ISourceChange arg0) {
						return internalPrintChangeTree(arg0);
					}});
				XList<String> lines = subLines.select(new Function<String, String>(){
					@Override
					public String apply(String s) {
						return '\t' + s;
					}});	
				lines.add(0, change.getSourceChangeLevel());
				return lines;
			}
			
			if(change.getSourceChangeType() == SourceChangeType.NULL)
				return XList.CreateList();
			
			XList<String> temp = new XList<String>();
			temp.add(change.getSourceChangeLevel() + ":" + change.getSourceChangeType().toString());
			return temp;
		}catch(Exception e)
		{
			Logger logger = ServiceLocator.ResolveType(Logger.class);
			logger.fatal(e);
			return new XList<String>();
		}
	}
}













