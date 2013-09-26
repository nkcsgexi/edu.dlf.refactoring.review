package edu.dlf.refactoring.ui;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FileUtils;

public class FakeCodeReviewInput implements ICodeReviewInput{

	private final String directory;
	private final ASTNode rootBefore;
	private final ASTNode rootAfter;

	@Inject
	public FakeCodeReviewInput() throws Exception
	{
		this.directory = System.getProperty("user.dir") + "/TestFiles/";
		this.rootBefore = ASTAnalyzer.parseICompilationUnit(FileUtils.readAll(
				this.directory + "TestCUBefore1.java"));
		this.rootAfter = ASTAnalyzer.parseICompilationUnit(FileUtils.readAll(
				this.directory + "TestCUAfter1.java"));
	}

	@Override
	public InputType getInputType() {
		return InputType.ASTNode;
	}

	@Override
	public Object getInputBefore() {
		return this.rootBefore;
	}

	@Override
	public Object getInputAfter() {
		return this.rootAfter;
	}

}
