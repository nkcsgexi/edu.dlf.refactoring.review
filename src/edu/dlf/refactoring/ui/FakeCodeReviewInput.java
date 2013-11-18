package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.utils.EclipseUtils;
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
	
	private List<P2<String, String>> comparedPairs = List.nil();
	private F<P2<String, String>, Boolean> hasPairNotCompared = 
		new F<P2<String, String>, Boolean>() {
		@Override
		public Boolean f(final P2<String, String> p1) {
			Option<P2<String, String>> op = comparedPairs.find(
				new F<P2<String,String>, Boolean>() {
				@Override
				public Boolean f(final P2<String, String> p2) {
					return p1._1().equals(p2._1()) && p1._2().equals(p2._2());
			}});
			return op.isNone();
	}}; 
	
	private F<String, Object> findProjectByName = new F<String, Object>() {
		@Override
		public Object f(String name) {
			Option<IJavaElement> finder = EclipseUtils.findJavaProjecInWorkspacetByName
				.f(name);
			if (finder.isNone())
				logger.fatal("Cannot find project with name: " + name);
			return finder.some();
	}}; 
	
	private F<P2<String, String>, P2<Object, Object>> getPairProjectsByNames = 
		FunctionalJavaUtil.extendMapper2Product(findProjectByName);
	
	
	private List<P2<IProject,IProject>> getAllProjectPairs() {
		List<IProject> projects = EclipseUtils.getAllImportedProjects();
		return projects.bind(projects, FunctionalJavaUtil.pairFunction((IProject)null)).
			removeAll(FunctionalJavaUtil.convertEqualToProduct(FunctionalJavaUtil.
				getReferenceEq((IProject)null)));
	}

	@Override
	public P2<Object, Object> getInputPair() {
		P2<IProject, IProject> input = getAllProjectPairs().head();
		P2<IJavaProject, IJavaProject> javaInput = P2.map(EclipseUtils.
			convertProject2JavaProject, input);
		return P.p((Object)javaInput._1(), (Object)javaInput._2());
	}
	


}
