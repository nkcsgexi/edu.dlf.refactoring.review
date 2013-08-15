package edu.dlf.refactoring.detectors;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.dlf.refactoring.change.SourceChangeUtils;
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
		XList<ISourceChange> allNodes = SourceChangeUtils.getSelfAndDescendent(root);
		return allNodes.where(new Predicate<ISourceChange>(){
			@Override
			public boolean apply(ISourceChange c) {
				return isChangeLevelOk(c.getSourceChangeLevel()) && isSourceChangeTypeOk
						(c.getSourceChangeType());
			}}).select(new Function<ISourceChange, IChangeSearchResult>(){
				@Override
				public IChangeSearchResult apply(final ISourceChange c) {
					return new IChangeSearchResult(){
						@Override
						public XList<ISourceChange> getSourceChanges() {
							return new XList<ISourceChange>(c);
						}};
				}});
	
	}
}
