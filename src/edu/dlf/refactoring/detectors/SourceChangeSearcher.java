package edu.dlf.refactoring.detectors;

import com.google.common.base.Function;

import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.utils.XList;

public class SourceChangeSearcher {
	
	private final XList<IChangeSearchCriteria> criterias
		= XList.CreateList();
	
	public void addSearchCriteria(IChangeSearchCriteria criteria)
	{
		criterias.add(criteria);
	}
	
	public void addSearchCriteria(final String changeLevel, final SourceChangeType type)
	{
		addSearchCriteria(new IChangeSearchCriteria() {
			@Override
			public SourceChangeType getSourceChangeType() {
				return type;
			}
			
			@Override
			public String getChangeLevel() {
				return changeLevel;
			}
		});
	}
	
	public XList<IChangeSearchResult> searchChanges(final ISourceChange change)
	{
		return criterias.select(new Function<IChangeSearchCriteria, 
				IChangeSearchResult>(){
			@Override
			public IChangeSearchResult apply(final IChangeSearchCriteria cri) {
				return new IChangeSearchResult(){
					final XList<ISourceChange> results;
					{
						results = search(change, cri);
					}
					@Override
					public IChangeSearchCriteria getCriteria() {
						return cri;
					}

					@Override
					public XList<ISourceChange> getSourceChanges() {
						return results;
					}};
			}});
	}

	private final XList<ISourceChange> search(ISourceChange parent, 
			IChangeSearchCriteria criteria)
	{
		XList<ISourceChange> results = XList.CreateList();
		XList<ISourceChange> allChanges = XList.CreateList();
		allChanges.add(parent);
		for(;allChanges.any();)
		{
			ISourceChange change = allChanges.remove(0);
			if(change.getSourceChangeType() == SourceChangeType.PARENT)
			{
				allChanges.addAll(((SubChangeContainer)change).getSubSourceChanges());
			}
			if(change.getSourceChangeLevel().equals(criteria.getChangeLevel())
				&& change.getSourceChangeType().equals(criteria.getSourceChangeType()))
			{
				results.add(change);
			}
		}
		return results;
	}
	
	
	public interface IChangeSearchCriteria
	{
		String getChangeLevel();
		SourceChangeType getSourceChangeType();
	}
	
	
	public interface IChangeSearchResult
	{
		IChangeSearchCriteria getCriteria();
		XList<ISourceChange> getSourceChanges();
	}
	
}
