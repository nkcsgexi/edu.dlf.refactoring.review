package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.utils.EclipseUtils;
import fj.Equal;
import fj.F;
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
	private F<P2<String, String>, Boolean> hasPairCompared = 
		new F<P2<String,String>, Boolean>() {
		@Override
		public Boolean f(final P2<String, String> p1) {
			Option<P2<String, String>> op = comparedPairs.find(
				new F<P2<String,String>, Boolean>() {
				@Override
				public Boolean f(P2<String, String> p2) {
					return p1._1().equals(p2._1()) && p1._2().equals(p2._2());
			}});
			return op.isSome();
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
	
	
	private List<P2<String, String>> getAllNamePairs() {
		List<String> names = EclipseUtils.getAllImportedProjects().map(
			new F<IProject, String>() {
				@Override
				public String f(IProject project) {
					return project.getName();
		}});
		return names.bind(names, FunctionalJavaUtil.pairFunction(""))
			.filter(FunctionalJavaUtil.convertEqualToProduct(Equal.stringEqual));
	}

	@Override
	public P2<Object, Object> getInputPair() {
		Option<P2<String, String>> op = getAllNamePairs().find(hasPairCompared);
		if(op.isSome()) {
			comparedPairs = comparedPairs.snoc(op.some());
			return getPairProjectsByNames.f(op.some());
		}
		return null;
	}
	


}
