package edu.dlf.refactoring.detectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ISourceChange.SourceChangeType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeCriteriaBuilder;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class CascadeChangeCriteriaBuilder implements IChangeCriteriaBuilder {
	
	private final Buffer<String> criteriaChain = Buffer.empty();
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public CascadeChangeCriteriaBuilder addSingleChangeCriteria(final String c,
			final SourceChangeType t) {
		criteriaChain.snoc("(@" + c + t.toString() + ")");
		return this;
	}
	
	public CascadeChangeCriteriaBuilder addOneOrMoreChangeCriteria(final String c, 
			final SourceChangeType t)
	{
		criteriaChain.snoc("(@" + c + t.toString() + ")+");
		return this;
	}
	
	
	public CascadeChangeCriteriaBuilder addZeroOrMoreChangeCriteria(final String c, 
			final SourceChangeType t)
	{
		criteriaChain.snoc("(@" + c + t.toString() + ")*");
		return this;
	}
	
	
	private String getElementaryChagneString(ISourceChange change)
	{
		return "@" + change.getSourceChangeLevel() + change.getSourceChangeType();
	}
	
	private String createChangeString(ISourceChange change)
	{
		StringBuilder sb = new StringBuilder();
		while(change != null)
		{
			sb.insert(0, getElementaryChagneString(change));
			change = change.getParentChange();
		}
		
		return sb.toString();
	}
	
	private List<ISourceChange> getChangesFromRootToLeaf(ISourceChange leaf)
	{
		Buffer<ISourceChange> buffer = Buffer.empty();
		while(leaf != null)
		{
			buffer.snoc(leaf);
			leaf = leaf.getParentChange();
		}
		return buffer.toList().reverse();
	}
	
	
	private String getCurrentSearchPattern()
	{
		return this.criteriaChain.toList().foldLeft(new F<StringBuilder, 
				F<String, StringBuilder>>(){
			@Override
			public F<String, StringBuilder> f(final StringBuilder sb) {
				return new F<String, StringBuilder>(){
					@Override
					public StringBuilder f(String s) {
						return sb.append(s);
					}};
			}}, new StringBuilder()).toString();
	}
	
	private List<IChangeSearchResult> getMatchedChangeChain(final String patternString, final ISourceChange leaf)
	{
		final List<ISourceChange> changeList = getChangesFromRootToLeaf(leaf);
		Buffer<String> matches = Buffer.empty();
		Pattern pattern = Pattern.compile(patternString);
		final String leafString = createChangeString(leaf);
		Matcher m = pattern.matcher(leafString);
		while(m.find())
		{
			matches.snoc(m.group(0));
		}
		return matches.toList().map(new F<String, List<ISourceChange>>(){
			@Override
			public List<ISourceChange> f(String s) {
				int start = StringUtils.countMatches(leafString.substring(0, leafString.indexOf(s)), "@");
				int length = StringUtils.countMatches(s, "@");
				return changeList.splitAt(start)._2().splitAt(length)._1();
			}}).map(new F<List<ISourceChange>, IChangeSearchResult>(){
					@Override
					public IChangeSearchResult f(final List<ISourceChange> list) {
						return new IChangeSearchResult() {
							@Override
							public List<ISourceChange> getSourceChanges() {
								return list;
							}
						};
					}});
	}
	
	
	@Override
	public IChangeSearchCriteria getSearchCriteria() {
		final String patternString = getCurrentSearchPattern();
		return new IChangeSearchCriteria() {
			@Override
			public List<IChangeSearchResult> search(ISourceChange root) {
				return SourceChangeUtils.getSelfAndDescendent(root).filter(new F<ISourceChange, Boolean>(){
					@Override
					public Boolean f(ISourceChange change) {
						return !change.hasSubChanges();
					}}).bind(new F<ISourceChange, List<IChangeSearchResult>>(){
						@Override
						public List<IChangeSearchResult> f(ISourceChange leaf) {
							return getMatchedChangeChain(patternString, leaf);
						}});
			}
		};
	}
}