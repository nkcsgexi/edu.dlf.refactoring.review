package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import fj.Equal;
import fj.P;
import fj.P2;
import fj.data.List;

public class FakeCodeReviewInput implements ICodeReviewInput {

	private final Logger logger;

	private final Equal<IJavaElement> projectEq =  Equal.stringEqual.comap
			(JavaModelAnalyzer.getElementNameFunc);
	
	@Inject
	public FakeCodeReviewInput(Logger logger) {
		this.logger = logger;
	}

	@Override
	public InputType getInputType() {
		return InputType.JavaElement;
	}
	
	private List<P2<IJavaElement, IJavaElement>> comparedPairs = List.nil();

	private List<P2<IJavaElement,IJavaElement>> getAllProjectPairs() {
		List<IJavaElement> projects = JavaModelAnalyzer.getJavaProjectsInWorkSpace();
		return projects.bind(projects, FunctionalJavaUtil.pairFunction((IJavaElement)null)).
			removeAll(FunctionalJavaUtil.convertEqualToProduct(FunctionalJavaUtil.
				getReferenceEq((IJavaElement)null)));
	}

	@Override
	public P2<Object, Object> getInputPair() {
		P2<IJavaElement, IJavaElement> input;
		List<P2<IJavaElement, IJavaElement>> remainingPairs = getAllProjectPairs().
			minus(FunctionalJavaUtil.extendEqual2Product(projectEq, projectEq), 
				comparedPairs);
		if(remainingPairs.isEmpty()) {
			comparedPairs = List.nil();
			input = getAllProjectPairs().head();
		} else {
			input = remainingPairs.head();
		}
		input = input.swap();
		comparedPairs = comparedPairs.snoc(input);
		logger.info("Projects to compare: " + input._1().getElementName() + 
			" and " + input._2().getElementName());
		return P.p((Object)input._1(), (Object)input._2());
	}

}
