package edu.dlf.refactoring.detectors;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchCriteria;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.F;
import fj.F2;
import fj.data.List;

public class ChangeSearchUtils {

	private ChangeSearchUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static final F<IChangeSearchResult, ISourceChange> getLeafSourceChangeFunc(){
		return new F<SourceChangeSearcher.IChangeSearchResult, ISourceChange>() {
			@Override
			public ISourceChange f(IChangeSearchResult result) {
				return result.getSourceChanges().last();
	}};}
	
	public static final F<ISourceChange, ASTNode> getNodeBeforeFunc = 
		new F<ISourceChange, ASTNode>() {
		@Override
		public ASTNode f(ISourceChange change) {
			return change.getNodeBefore();
	}}; 
	
	public static final F<ISourceChange, ASTNode> getNodeAfterFunc = 
			new F<ISourceChange, ASTNode>() {
			@Override
			public ASTNode f(ISourceChange change) {
				return change.getNodeAfter();
	}};
	
	public static final F2<ISourceChange, IChangeSearchCriteria, List<IChangeSearchResult>> 
		searchFunc = new F2<ISourceChange, IChangeSearchCriteria, 
		List<IChangeSearchResult>>() {
		@Override
		public List<IChangeSearchResult> f(ISourceChange change, 
			IChangeSearchCriteria criteria) {
				return criteria.search(change);
	}};
}
