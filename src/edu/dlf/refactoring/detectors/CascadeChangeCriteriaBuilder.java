package edu.dlf.refactoring.detectors;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeCriteriaBuilder;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import edu.dlf.refactoring.utils.XList;

public class CascadeChangeCriteriaBuilder implements IChangeCriteriaBuilder {
	private final XList<BasicChangeSearchCriteria> criteriaChain = XList
			.CreateList();

	public CascadeChangeCriteriaBuilder addNextChangeCriteria(final String c,
			final SourceChangeType t) {
		this.criteriaChain.add(new BasicChangeSearchCriteria() {
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
		final XList<BasicChangeSearchCriteria> chain = new XList(
				this.criteriaChain);
		return new IChangeSearchCriteria() {

			private boolean doesChangeMeetBasicCriteria(
					BasicChangeSearchCriteria criteria, ISourceChange change) {
				return criteria.isChangeLevelOk(change.getSourceChangeLevel())
						&& criteria.isSourceChangeTypeOk(change
								.getSourceChangeType());
			}

			@Override
			public XList<IChangeSearchResult> getChangesMeetCriteria(
					ISourceChange root) {
				XList<ISourceChange> nodes = SourceChangeUtils
						.getSelfAndDescendent(root);
				return nodes.where(new Predicate<ISourceChange>() {
					@Override
					public boolean apply(ISourceChange change) {
						for (int i = chain.size() - 1; i >= 0; i++) {
							if (!doesChangeMeetBasicCriteria(chain.get(i),
									change))
								return false;
						}
						return true;
					}
				}).select(new Function<ISourceChange, IChangeSearchResult>() {
					@Override
					public IChangeSearchResult apply(ISourceChange change) {
						final XList<ISourceChange> changes = new XList<ISourceChange>();
						for (int i = 0; i < chain.size(); i++) {
							changes.add(change);
							change = change.getParentChange();
						}
						return new IChangeSearchResult() {
							@Override
							public XList<ISourceChange> getSourceChanges() {
								return changes;
							}
						};
					}
				});
			}
		};
	}
}