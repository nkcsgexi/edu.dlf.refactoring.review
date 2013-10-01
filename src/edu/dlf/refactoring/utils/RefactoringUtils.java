package edu.dlf.refactoring.utils;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.PerformChangeOperation;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.data.Option;

public class RefactoringUtils {

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public static Option<Change> createChange(Refactoring ref)
	{
		try{
			RefactoringStatus status = ref.checkAllConditions(new 
					NullProgressMonitor());
			if(status.isOK()){
				return Option.some(ref.createChange(new NullProgressMonitor()));
			} else
			{
				throw new Exception();
			}
		}catch(Exception e)
		{
			logger.fatal("Check refactoring condition fails:" + e);
			return Option.some(null);
		}
	}
	
	public static void performChange(Change change)
	{
		try {
			PerformChangeOperation op = new PerformChangeOperation(change);
			op.run(new NullProgressMonitor());
		} catch (Exception e) {
			logger.fatal(e);
		}
	}
}
