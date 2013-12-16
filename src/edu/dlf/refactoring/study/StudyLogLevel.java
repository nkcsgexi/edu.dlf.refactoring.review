package edu.dlf.refactoring.study;

import org.apache.log4j.Level;

public class StudyLogLevel extends Level {

	public static Level LEVEL = new StudyLogLevel();

	private StudyLogLevel() {
		super(FATAL_INT + 1, "STUDY", 7);
	}
}
