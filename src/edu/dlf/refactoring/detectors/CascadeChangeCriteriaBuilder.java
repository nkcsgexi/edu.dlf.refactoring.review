package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeCriteriaBuilder;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class CascadeChangeCriteriaBuilder implements IChangeCriteriaBuilder {
	private final Buffer<BasicChangeSearchCriteria> criteriaChain = Buffer.empty();

	public CascadeChangeCriteriaBuilder addNextChangeCriteria(final String c,
			final SourceChangeType t) {
		this.criteriaChain.snoc(new BasicChangeSearchCriteria() {
			@Override
			protected boolean isChangeLevelOk(String changeLevel) {
				return changeLevel.equals(c);
			}

			@Override
			protected boolean isSourceChangeTypeOk(SourceChangeType type) {
				return type == t;
			}
		});
		return this;
	}

	@Override
	public IChangeSearchCriteria getSearchCriteria() {
		final List<BasicChangeSearchCriteria> chain = criteriaChain.toList();
		return new IChangeSearchCriteria() {

			private boolean doesChangeMeetBasicCriteria(
					BasicChangeSearchCriteria criteria, ISourceChange change) {
				return criteria.isChangeLevelOk(change.getSourceChangeLevel())
						&& criteria.isSourceChangeTypeOk(change
								.getSourceChangeType());
			}

			@Override
			public List<IChangeSearchResult> getChangesMeetCriteria(
					ISourceChange root) {
				List<ISourceChange> nodes = SourceChangeUtils
						.getSelfAndDescendent(root);
				return nodes.filter(new F<ISourceChange, Boolean>() {
					@Override
					public Boolean f(ISourceChange change) {
						for (int i = chain.length() - 1; i >= 0; i++) {
							if (!doesChangeMeetBasicCriteria(chain.index(i),
									change))
								return false;
						}
						return true;
					}
				}).map(new F<ISourceChange, IChangeSearchResult>() {
					@Override
					public IChangeSearchResult f(ISourceChange change) {
						final Buffer<ISourceChange> changes = Buffer.empty();
						for (int i = 0; i < chain.length(); i++) {
							changes.snoc(change);
							change = change.getParentChange();
						}
						return new IChangeSearchResult() {
							@Override
							public List<ISourceChange> getSourceChanges() {
								return changes.toList();
							}
						};
					}
				});
			}
		};
	}
}