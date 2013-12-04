package edu.dlf.refactoring.study;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.design.DesignUtils;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator.ChangeCompAnnotation;
import fj.Effect;
import fj.Equal;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.Unit;
import fj.data.List;

public class CompareProjectsInWorkspaceStudy extends AbstractStudy{

	private final IFactorComponent changeComp;
	private final Logger logger;
	
	@Inject
	public CompareProjectsInWorkspaceStudy(
			Logger logger,
			@ChangeCompAnnotation IFactorComponent changeComp) {
		super("Compare projects in work space");
		this.changeComp = changeComp;
		this.logger = logger;
	}
	
	private final F<IJavaElement, String> getOriginalName = 
		new F<IJavaElement, String>() {
		@Override
		public String f(IJavaElement element) {
			return element.getElementName().replaceAll("\\d*$", "");
	}};
	
	private final Equal<IJavaElement> projectEq = Equal.stringEqual.comap
		(getOriginalName);
	
	private final Ord<IJavaElement> projectOrd = Ord.stringOrd.comap
		(getOriginalName);

	private final Effect<P2<IJavaElement, IJavaElement>> printProjectPair =
		new Effect<P2<IJavaElement,IJavaElement>>() {
			@Override
			public void e(P2<IJavaElement, IJavaElement> p) {
				logger.info("Project before: " + p._1().getElementName());
				logger.info("Project after: " + p._2().getElementName());		
	}};
	
	@Override
	protected void study() {
		final List<IJavaElement> projects = JavaModelAnalyzer.
			getJavaProjectsInWorkSpace();
		final List<List<IJavaElement>> groups = projects.sort(projectOrd).
			group(projectEq);
		final Ord<IJavaElement> timeOrd = Ord.stringOrd.comap(JavaModelAnalyzer.
			getElementNameFunc);
		groups.foreach(new Effect<List<IJavaElement>>() {
			@Override
			public void e(List<IJavaElement> projects) {
				projects = projects.sort(timeOrd);
				F<Object, Unit> feeder = DesignUtils.feedComponent.flip().
					f(changeComp);
				F<JavaElementPair, Object> converter = FJUtils.getTypeConverter
					((JavaElementPair)null, (Object)null);
				List<P2<IJavaElement, IJavaElement>> projectPairs = projects.
					zip(projects.drop(1));
				projectPairs.foreach(printProjectPair);
				projectPairs.map(DesignUtils.convertProduct2JavaElementPair).
					map(converter.andThen(feeder));
		}});
	}

}