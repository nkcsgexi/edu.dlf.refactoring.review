package edu.dlf.refactoring.refactorings;

import edu.dlf.refactoring.design.IDetectedRefactoring;

public class DetectedRefactoringUtils {
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
}
