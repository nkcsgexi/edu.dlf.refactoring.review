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

	private Object findProjectByName(String name) {
		Option<IJavaElement> finder = EclipseUtils.findJavaProjecInWorkspacetByName
			.f(name);
		if (finder.isNone())
			logger.fatal("Cannot find project with name: " + name);
		return finder.some();
	}

	@Override
	public P2<Object, Object> getInputPair() {
		return null;
	}
	


}
