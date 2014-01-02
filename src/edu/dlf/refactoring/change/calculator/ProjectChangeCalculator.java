package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.DlfStringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import fj.Effect;
import fj.Equal;
import fj.P2;
import fj.data.List;

public class ProjectChangeCalculator implements IJavaModelChangeCalculator{
	
	private final IJavaModelChangeCalculator pChangeCalculator;
	private final String projectLevel;
	private final Logger logger;

	@Inject
	public ProjectChangeCalculator(
			@JavaProjectAnnotation String projectLevel,
			@SourcePackageAnnotation IJavaModelChangeCalculator pChangeCalculator,
			Logger logger) {
		this.projectLevel = projectLevel;
		this.pChangeCalculator = pChangeCalculator;
		this.logger = logger;
	}
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		logger.info("Compare projects: " + pair.getElementBefore().
			getElementName() + " " + pair.getElementAfter().getElementName());
		final SubChangeContainer container = new SubChangeContainer(this.
			projectLevel, pair);
		List<IJavaElement> packagesBefore = JavaModelAnalyzer.getSourcePackages
			(pair.getElementBefore());
		List<IJavaElement> packagesAfter = JavaModelAnalyzer.getSourcePackages
			(pair.getElementAfter());
		List<P2<IJavaElement, IJavaElement>> packagePairs = FJUtils.getSamePairs
			(packagesBefore, packagesAfter, Equal.stringEqual.comap(
				JavaModelAnalyzer.getElementNameFunc));
		packagePairs.foreach(new Effect<P2<IJavaElement, IJavaElement>>() {
			@Override
			public void e(P2<IJavaElement, IJavaElement> p) {
				container.addSubChange(pChangeCalculator.
					CalculateJavaModelChange(new JavaElementPair(p._1(), 
						p._2())));}});
		return container;
	}

	private void logPackageNames(List<IJavaElement> packages) {
		List<String> names = packages.map(JavaModelAnalyzer.getElementNameFunc);
		logger.info("Package names:");
		for(String name : names.toCollection()) {
			logger.info(name);
		}
	}
}
