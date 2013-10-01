package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.JavaProjectAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import fj.Effect;
import fj.P2;

public class ProjectChangeCalculator implements IJavaModelChangeCalculator{
	
	private final IJavaModelChangeCalculator pChangeCalculator;
	private final String projectLevel;

	@Inject
	public ProjectChangeCalculator(
		@JavaProjectAnnotation String projectLevel,
		@SourcePackageAnnotation IJavaModelChangeCalculator pChangeCalculator)
	{
		this.projectLevel = projectLevel;
		this.pChangeCalculator = pChangeCalculator;
	}
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		final SubChangeContainer container = new SubChangeContainer
				(this.projectLevel, pair);
		JavaModelAnalyzer.getSameNameElementPairsFunction().
			f(JavaModelAnalyzer.getSourcePackages(pair.getElementBefore()), 
				JavaModelAnalyzer.getSourcePackages(pair.getElementAfter())).
					foreach(new Effect<P2<IJavaElement, IJavaElement>>() {
						@Override
						public void e(P2<IJavaElement, IJavaElement> p) {
							container.addSubChange(pChangeCalculator.
								CalculateJavaModelChange(new JavaElementPair
									(p._1(), p._2())));}});
		return container;
	}
}
