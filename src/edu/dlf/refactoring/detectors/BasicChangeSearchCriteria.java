package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.utils.XList;

public abstract class BasicChangeSearchCriteria implements IChangeSearchCriteria
{
	protected abstract boolean isChangeLevelOk(String changeLevel);
	protected abstract boolean isSourceChangeTypeOk(SourceChangeType type);
	
	@Override
	public XList<IChangeSearchResult> getChangesMeetCriteria(ISourceChange root)
	{
		final XList<ISourceChange> results = XList.CreateList();
		if (this.isChangeLevelOk(root.getSourceChangeLevel()) && 
				this.isSourceChangeTypeOk(root.getSourceChangeType()))
		{
			results.add(root);
		}
		return results.any() ? new XList<IChangeSearchResult>(new IChangeSearchResult(){
			@Override
			public XList<ISourceChange> getSourceChanges() {
				return results;
			}}) : new XList<IChangeSearchResult>();
	}
}
