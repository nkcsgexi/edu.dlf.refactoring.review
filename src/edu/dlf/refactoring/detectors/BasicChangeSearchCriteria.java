package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public abstract class BasicChangeSearchCriteria implements IChangeSearchCriteria
{
	protected abstract boolean isChangeLevelOk(String changeLevel);
	protected abstract boolean isSourceChangeTypeOk(SourceChangeType type);
	
	@Override
	public List<IChangeSearchResult> getChangesMeetCriteria(ISourceChange root)
	{	
		List<ISourceChange> allNodes = SourceChangeUtils.getSelfAndDescendent(root);
		return allNodes.filter(new F<ISourceChange, Boolean>(){
			@Override
			public Boolean f(ISourceChange change) {
				return isChangeLevelOk(change.getSourceChangeLevel()) &&
						isSourceChangeTypeOk(change.getSourceChangeType());
			}}).map(new F<ISourceChange, IChangeSearchResult>(){
				@Override
				public IChangeSearchResult f(final ISourceChange c) {
					return new IChangeSearchResult(){
						@Override
						public List<ISourceChange> getSourceChanges() {
							Buffer<ISourceChange> buffer = Buffer.empty();
							buffer.snoc(c);
							return buffer.toList();
						}};
				}});
	}
}
