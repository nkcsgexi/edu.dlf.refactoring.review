package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;
import fj.data.List;

public class FakeCodeReviewInput implements ICodeReviewInput{

	private final Logger logger;
	private final List<IJavaElement> allProjects;
	
	@Inject
	public FakeCodeReviewInput() throws Exception
	{
		this.logger = ServiceLocator.ResolveType(Logger.class);
		this.allProjects = JavaModelAnalyzer.getJavaProjectsInWorkSpace();
	}

	@Override
	public InputType getInputType() {
		return InputType.JavaElement;
	}

	@Override
	public Object getInputBefore() {
		return this.allProjects.find(new F<IJavaElement, Boolean>(){
			@Override
			public Boolean f(IJavaElement element) {
				return element.getElementName().equals("FakeBefore");
			}}).some();
	}

	@Override
	public Object getInputAfter() {
		return this.allProjects.find(new F<IJavaElement, Boolean>(){
			@Override
			public Boolean f(IJavaElement element) {
				return element.getElementName().equals("FakeAfter");
			}}).some();
	}
	


}
