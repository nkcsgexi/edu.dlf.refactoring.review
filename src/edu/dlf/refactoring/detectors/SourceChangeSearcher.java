package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;

public class SourceChangeSearcher {
	
	public interface IChangeSearchCriteria
	{
		XList<IChangeSearchResult> getChangesMeetCriteria(ISourceChange root); 
	}
	
	public interface IChangeCriteriaBuilder
	{
		IChangeSearchCriteria getSearchCriteria();
	}
	
	public interface IChangeSearchResult
	{
		XList<ISourceChange> getSourceChanges();
	}
	
}
