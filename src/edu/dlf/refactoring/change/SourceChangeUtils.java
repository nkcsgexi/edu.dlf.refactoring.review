package edu.dlf.refactoring.change;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.google.common.base.Function;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;
import edu.dlf.refactoring.utils.XList.IAggregator;

public class SourceChangeUtils {

	private SourceChangeUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static ISourceChange pruneSourceChange(ISourceChange change)
	{
		pruneSubChanges(change);
		return change;
	}
	
	public static XList<ISourceChange> getSelfAndDescendent(ISourceChange root)
	{
		XList<ISourceChange> all = XList.CreateList();
		XList<ISourceChange> unHandled = new XList<ISourceChange>(root);
		while(unHandled.any())
		{
			ISourceChange first = unHandled.remove(0);
			all.add(first);
			unHandled.addAll(first.getSubSourceChanges());
		}
		
		return all;
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













