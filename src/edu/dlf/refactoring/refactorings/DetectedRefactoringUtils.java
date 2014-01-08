package edu.dlf.refactoring.refactorings;

import org.apache.log4j.Logger;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ServiceLocator;

public class DetectedRefactoringUtils {
	
	private static final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private DetectedRefactoringUtils() throws Exception {
		throw new Exception();
	}
	
	public static boolean isRenameRefactoring(IDetectedRefactoring refactoring) {
		return refactoring instanceof AbstractDetectedRenameRefactoring;
	}
	
	public static String getOldName(IDetectedRefactoring refactoring) {
		 return ((AbstractDetectedRenameRefactoring)refactoring).getNameBefore();
	}
	
	public static String getNewName(IDetectedRefactoring refactoring) {
		 return ((AbstractDetectedRenameRefactoring)refactoring).getNameAfter();
	}
	
	public static String getRenamedEntityType(IDetectedRefactoring refactoring) {
		if(refactoring instanceof DetectedRenameField) {
			return "Field";
		}
		if(refactoring instanceof DetectedRenameLocalVariable) {
			return "Local variable";
		}
		if(refactoring instanceof DetectedRenameMethodRefactoring) {
			return "Method";
		}
		if(refactoring instanceof DetectedRenameTypeRefactoring) {
			return "Type";
		}
		logger.fatal("Unknown rename type.");
		return "";
	}
}
