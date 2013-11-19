package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.utils.EclipseUtils;
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
	
	private List<P2<IProject, IProject>> comparedPairs = List.nil();
	private F<P2<IProject, IProject>, Boolean> hasPairNotCompared = 
		new F<P2<IProject, IProject>, Boolean>() {
		@Override
		public Boolean f(final P2<IProject, IProject> pair) {
			Equal<IProject> eq = FunctionalJavaUtil.getReferenceEq((IProject)null);
			F<P2<IProject, IProject>, Boolean> selector = FunctionalJavaUtil.
				extendEqual2Product(eq, eq).eq(pair);
			return comparedPairs.find(selector).isNone();
	}}; 
	
	private List<P2<IProject,IProject>> getAllProjectPairs() {
		List<IProject> projects = EclipseUtils.getAllImportedProjects();
		return projects.bind(projects, FunctionalJavaUtil.pairFunction((IProject)null)).
			removeAll(FunctionalJavaUtil.convertEqualToProduct(FunctionalJavaUtil.
				getReferenceEq((IProject)null)));
	}

	@Override
	public P2<Object, Object> getInputPair() {
		Option<P2<IProject, IProject>> option = getAllProjectPairs().find(
			hasPairNotCompared);
		if(option.isNone()) {
			comparedPairs = List.nil();
			option = Option.some(getAllProjectPairs().head());
		}
		P2<IProject, IProject> input = option.some();
		comparedPairs = comparedPairs.snoc(input);
		P2<IJavaProject, IJavaProject> javaInput = P2.map(EclipseUtils.
			convertProject2JavaProject, input);
		return P.p((Object)javaInput._1(), (Object)javaInput._2());
	}
	


}
