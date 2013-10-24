package edu.dlf.refactoring.detectors;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.detectors.SourceChangeSearcher.IChangeSearchResult;
import fj.F;

public class ChangeSearchUtils {

	private ChangeSearchUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static F<IChangeSearchResult, ISourceChange> getLeafSourceChangeFunc(){
		return new F<SourceChangeSearcher.IChangeSearchResult, ISourceChange>() {
			@Override
			public ISourceChange f(IChangeSearchResult result) {
				return result.getSourceChanges().last();
			}
		};
	}
	
	
	
}
