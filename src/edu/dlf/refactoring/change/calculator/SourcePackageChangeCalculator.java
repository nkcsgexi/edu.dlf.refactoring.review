package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import fj.Effect;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class SourcePackageChangeCalculator implements IJavaModelChangeCalculator{

	private final IJavaModelChangeCalculator cuCalculator;
	private final String paLevel;
	private final Logger logger;
	
	@Inject
	public SourcePackageChangeCalculator(
			@SourcePackageAnnotation String paLevel,
			@CompilationUnitAnnotation IJavaModelChangeCalculator cuCalculator,
			Logger logger)
	{
		this.paLevel = paLevel;
		this.cuCalculator = cuCalculator;
		this.logger = logger;
	}
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		logger.debug("Compare packages: " + pair.getElementBefore().getElementName() + " " 
				+ pair.getElementAfter().getElementName());
		final SubChangeContainer change = new SubChangeContainer(this.paLevel, pair);
		JavaModelAnalyzer.getSameNameElementPairsFunction().f(JavaModelAnalyzer.
			getICompilationUnit(pair.getElementBefore()), JavaModelAnalyzer.
				getICompilationUnit(pair.getElementAfter())).foreach(
					new Effect<P2<IJavaElement, IJavaElement>>() {
					@Override
					public void e(P2<IJavaElement, IJavaElement> p) {
						change.addSubChange(cuCalculator.CalculateJavaModelChange
							(new JavaElementPair(p._1(), p._2())));
		}});
		List<IJavaElement> addedUnits = JavaModelAnalyzer.getAddedElementsFunction().
			f(JavaModelAnalyzer.getICompilationUnit(pair.getElementBefore()), 
				JavaModelAnalyzer.getICompilationUnit(pair.getElementAfter()));
		List<IJavaElement> removedUnits = JavaModelAnalyzer.getRemovedElementsFunction().
			f(JavaModelAnalyzer.getICompilationUnit(pair.getElementBefore()), 
				JavaModelAnalyzer.getICompilationUnit(pair.getElementAfter()));
		
		List<P2<IJavaElement, IJavaElement>> similarPairs = JavaModelAnalyzer.
			getSimilarJavaElement(removedUnits, addedUnits, 8, 
				new F2<IJavaElement, IJavaElement, Integer>() {
			@Override
			public Integer f(IJavaElement arg0, IJavaElement arg1) {
				String n1 = arg0.getElementName();
				String n2 = arg1.getElementName();
				int score = (int) (XStringUtils.getSamePartPercentage.f(n1, n2) * 10);
				logger.debug("Name similarity score: " + score);
				return score;
		}});
		
		Effect<P2<IJavaElement, IJavaElement>> calculateSubchange = 
			new Effect<P2<IJavaElement, IJavaElement>>() {
			@Override
			public void e(P2<IJavaElement, IJavaElement> p) {
				change.addSubChange(cuCalculator.CalculateJavaModelChange(
					new JavaElementPair(p._1(), p._2())));
		}};
	
		similarPairs.foreach(calculateSubchange);
		Equal<IJavaElement> eq = Equal.stringEqual.comap(JavaModelAnalyzer.
			getElementNameFunc);
		F<P2<IJavaElement, IJavaElement>, IJavaElement> getFirst = FunctionalJavaUtil.
			getFirstElementInPFunc((IJavaElement)null, (IJavaElement)null);
		F<P2<IJavaElement, IJavaElement>, IJavaElement> getSecond = FunctionalJavaUtil.
			getSecondElementInPFunc((IJavaElement)null, (IJavaElement)null);
		removedUnits = removedUnits.minus(eq, similarPairs.map(getFirst));
		addedUnits = addedUnits.minus(eq, similarPairs.map(getSecond));
		removedUnits.map(FunctionalJavaUtil.appendElementFunc((IJavaElement)null, 
			(IJavaElement)null)).foreach(calculateSubchange);
		addedUnits.map(FunctionalJavaUtil.prependElementFunc((IJavaElement)null, 
			(IJavaElement)null)).foreach(calculateSubchange);
		return change;
	}

}
