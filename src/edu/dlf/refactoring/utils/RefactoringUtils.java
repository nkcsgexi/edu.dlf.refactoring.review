package edu.dlf.refactoring.utils;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.data.Option;

public class RefactoringUtils {

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public static Option<Change> createChange(Refactoring ref)
	{
		try{
			ref.checkAllConditions(new NullProgressMonitor());
			return Option.some(ref.createChange(new NullProgressMonitor()));
		}catch(Exception e)
		{
			logger.fatal(e);
			return Option.some(null);
		}
	}
}
