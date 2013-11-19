package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import fj.Equal;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;

public class FakeCodeReviewInput implements ICodeReviewInput {

	private final Logger logger;

	@Inject
	public FakeCodeReviewInput(Logger logger) {
		this.logger = logger;
	}

	@Override
	public InputType getInputType() {
		return InputType.JavaElement;
	}
	
	private List<P2<IJavaElement, IJavaElement>> comparedPairs = List.nil();
	private F<P2<IJavaElement, IJavaElement>, Boolean> hasPairNotCompared = 
		new F<P2<IJavaElement, IJavaElement>, Boolean>() {
		@Override
		public Boolean f(final P2<IJavaElement, IJavaElement> pair) {
			Equal<IJavaElement> eq = FunctionalJavaUtil.getReferenceEq((IJavaElement)null);
			F<P2<IJavaElement, IJavaElement>, Boolean> selector = FunctionalJavaUtil.
				extendEqual2Product(eq, eq).eq(pair);
			return comparedPairs.find(selector).isNone();
	}}; 
	
	private List<P2<IJavaElement,IJavaElement>> getAllProjectPairs() {
		List<IJavaElement> projects = JavaModelAnalyzer.getJavaProjectsInWorkSpace();
		return projects.bind(projects, FunctionalJavaUtil.pairFunction((IJavaElement)null)).
			removeAll(FunctionalJavaUtil.convertEqualToProduct(FunctionalJavaUtil.
				getReferenceEq((IJavaElement)null)));
	}

	@Override
	public P2<Object, Object> getInputPair() {
		Option<P2<IJavaElement, IJavaElement>> option = getAllProjectPairs().find(
			hasPairNotCompared);
		if(option.isNone()) {
			comparedPairs = List.nil();
			option = Option.some(getAllProjectPairs().head());
		}
		P2<IJavaElement, IJavaElement> input = option.some();
		comparedPairs = comparedPairs.snoc(input);
		return P.p((Object)input._1(), (Object)input._2());
	}
	


}
