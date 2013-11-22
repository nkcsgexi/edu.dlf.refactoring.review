package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import fj.Equal;
import fj.P;
import fj.P2;
import fj.data.List;

public class FakeCodeReviewInput implements ICodeReviewInput {

	private final Logger logger;
	
	private int index = 1;

	private final Equal<IJavaElement> projectEq =  Equal.stringEqual.comap
			(JavaModelAnalyzer.getElementNameFunc);
	
	private final Equal<P2<IJavaElement, IJavaElement>> projectPEqual = 
		FJUtils.extendEqualToProduct(projectEq, projectEq); 
	
	private List<P2<IJavaElement, IJavaElement>> comparedPairs = List.nil();

	@Inject
	public FakeCodeReviewInput(Logger logger) {
		this.logger = logger;
	}

	@Override
	public InputType getInputType() {
		return InputType.JavaElement;
	}

	private List<P2<IJavaElement,IJavaElement>> getAllProjectPairs() {
		List<IJavaElement> projects = JavaModelAnalyzer.getJavaProjectsInWorkSpace();
		return projects.bind(projects, FJUtils.pairFunction((IJavaElement)null)).
			removeAll(FJUtils.convertEqualToProduct(FJUtils.
				getReferenceEq((IJavaElement)null)));
	}

	@Override
	public P2<Object, Object> getInputPair() {
		List<P2<IJavaElement, IJavaElement>> allProjects = getAllProjectPairs();
		P2<IJavaElement, IJavaElement> input = allProjects.index(index);
		index += 1;
		if(index == allProjects.length()) {
			index = 1;
		}
		logger.info("Projects to compare: " + input._1().getElementName() + 
			" and " + input._2().getElementName());
		return P.p((Object)input._1(), (Object)input._2());
	}

}
