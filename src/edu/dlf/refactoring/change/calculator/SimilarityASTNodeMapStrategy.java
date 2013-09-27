package edu.dlf.refactoring.change.calculator;

import java.util.Comparator;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.utils.XList;

public class SimilarityASTNodeMapStrategy implements IASTNodeMapStrategy{

	private final IDistanceCalculator calculator;	
	
	public interface IDistanceCalculator
	{
		int calculateDistance(ASTNode before, ASTNode after);
	}
	
	public SimilarityASTNodeMapStrategy(IDistanceCalculator calculator)
	{
		this.calculator = calculator;
	}
	
	
	private class Entry
	{
		public int row;
		public int column;
		public int value;
		
		public boolean IsConflict(Entry another)
		{
			return column == another.column || row == another.row;
		}
	}
	
	
	@Override
	public XList<ASTNodePair> map(final XList<ASTNode> beforeNodes, final XList<ASTNode> afterNodes) {
	
		XList<Entry> entries = new XList<Entry>();
			
		for(int i = 0; i < beforeNodes.size() ; i ++)
		{
			for(int j = 0; j < afterNodes.size(); j ++)
			{
				int value = calculator.calculateDistance(beforeNodes.get(i), 
						afterNodes.get(j));
				Entry e = new Entry();
				e.row = i;
				e.column = j;
				e.value = value;
				entries.add(e);
			}
		}
		
		entries = entries.orderBy(new Comparator<Entry>(){
			@Override
			public int compare(Entry e1, Entry e2) {
				return e1.value - e2.value;
			}});
		
		
		XList<Entry> matched = new XList<Entry>();
		
		for(final Entry e : entries)
		{
			if(matched.any(new Predicate<Entry>(){
				@Override
				public boolean apply(Entry another) {
					return another.IsConflict(e) ;
				}}))
			{
				continue;
			}
			matched.add(e);
		}
		
		XList<ASTNodePair> results = new XList<ASTNodePair>();
		
		final XList<ASTNodePair> matchedPairs = matched.select(new Function<Entry, ASTNodePair>(){
			@Override
			public ASTNodePair apply(Entry e) {
				return new ASTNodePair(beforeNodes.get(e.row), afterNodes.get(e.column));
		}});
		
		results.addAll(matchedPairs);
		
		
		if(beforeNodes.size() > afterNodes.size())
		{
			results.addAll(beforeNodes.where(new Predicate<ASTNode>(){
				@Override
				public boolean apply(ASTNode node) {
					for(ASTNodePair pair : matchedPairs)
					{
						if(pair.getNodeBefore() == node)
							return false;
					}
					return true;
				}}).select(new Function<ASTNode, ASTNodePair>(){
					@Override
					public ASTNodePair apply(ASTNode node) {
						return new ASTNodePair(node, null);
					}}));
		} 
		
		if(beforeNodes.size() < afterNodes.size())
		{
			results.addAll(afterNodes.where(new Predicate<ASTNode>(){
				@Override
				public boolean apply(ASTNode node) {
					for(ASTNodePair pair : matchedPairs)
					{
						if(pair.getNodeAfter() == node)
							return false;
					}
					return true;
				}}).select(new Function<ASTNode, ASTNodePair>(){
					@Override
					public ASTNodePair apply(ASTNode node) {
						return new ASTNodePair(null, node);
					}}));
		}
		return results;
	}
	

}
