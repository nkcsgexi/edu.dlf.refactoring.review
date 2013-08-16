package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.utils.XList;
import fj.data.List;

public class SourceChangeSearcher {
	
	public interface IChangeSearchCriteria
	{
		List<IChangeSearchResult> getChangesMeetCriteria(ISourceChange root); 
	}
	
	public interface IChangeCriteriaBuilder
	{
		IChangeSearchCriteria getSearchCriteria();
	}
	
	public interface IChangeSearchResult
	{
		List<ISourceChange> getSourceChanges();
	}
	
}
