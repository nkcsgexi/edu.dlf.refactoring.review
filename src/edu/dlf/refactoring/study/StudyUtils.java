package edu.dlf.refactoring.study;

import org.apache.log4j.Logger;

import difflib.Delta.TYPE;
import edu.dlf.refactoring.change.ChangedLinesComputer;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.refactorings.DetectedRefactoringUtils;
import fj.Effect;
import fj.F;
import fj.P2;
import fj.data.List;

public class StudyUtils {
	
	private StudyUtils() throws Exception {
		throw new Exception();
	}

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	private static ChangedLinesComputer computer = ServiceLocator.ResolveType
		(ChangedLinesComputer.class);
	
	private static void writeStudylog(String message) {
		logger.log(StudyLogLevel.LEVEL, message);
	}
	
	
	public static void logRevisionStart(){
		writeStudylog("===========================================");
	}
	
	
	public static final Effect<ISourceChange> logChangedLines = new Effect
		<ISourceChange>() {
		@Override
		public void e(ISourceChange change) {
			computer.startCompute(change);
			writeStudylog("All changes: " + change.getElementBefore().
				getElementName() + "->" + change.getElementAfter().
					getElementName());
			writeStudylog("Changed lines: " + computer.getChangedLines());
			writeStudylog("Added lines: " + computer.getAddedLines());
			writeStudylog("Removed lines: " + computer.getRemovedLines());
	}}; 
	
	private static F<P2<TYPE, Integer>, String> getHeader = 
		new F<P2<TYPE, Integer>, String>(){
		@Override
		public String f(P2<TYPE, Integer> p) {
			switch(p._1()){
			case INSERT:
				return "Inserted lines: " + p._2();
			case DELETE:
				return "Removed lines: " + p._2();
			case CHANGE:
				return "Changed lines: " + p._2();
			}
			return "";
		}};
	
	public static final Effect<IDetectedRefactoring> logDetectedRefactoring =
		new Effect<IDetectedRefactoring>() {
			@Override
			public void e(IDetectedRefactoring refactoring) {
				logger.fatal(refactoring.getRefactoringType());
				if(DetectedRefactoringUtils.isRenameRefactoring(refactoring)) {
					writeStudylog(DetectedRefactoringUtils.getOldName(refactoring)
						+ "->" + DetectedRefactoringUtils.getNewName(refactoring));
				}
				List<P2<TYPE, Integer>> summary = refactoring.getDeltaSummary();
				summary.map(getHeader).foreach(new Effect<String>() {
					@Override
					public void e(String line) {
						writeStudylog(line);
				}});
	}};
}
