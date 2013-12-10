package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.analyzers.DlfStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
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
	private final ChangeBuilder changeBuilder;
	
	@Inject
	public SourcePackageChangeCalculator(
			@SourcePackageAnnotation String paLevel,
			@CompilationUnitAnnotation IJavaModelChangeCalculator cuCalculator,
			Logger logger)
	{
		this.paLevel = paLevel;
		this.cuCalculator = cuCalculator;
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(paLevel);
	}
	
	final Equal<IJavaElement> eq = Equal.stringEqual.comap(JavaModelAnalyzer.
			getElementNameFunc);
	
	final F<P2<IJavaElement, IJavaElement>, IJavaElement> getFirst = FJUtils.
			getFirstElementInPFunc((IJavaElement)null, (IJavaElement)null);
	
	final F<P2<IJavaElement, IJavaElement>, IJavaElement> getSecond = FJUtils.
			getSecondElementInPFunc((IJavaElement)null, (IJavaElement)null);
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		logger.info("Compare packages: " + pair.getElementBefore().getElementName() 
				+ ":" + pair.getElementAfter().getElementName());
		final SubChangeContainer change = new SubChangeContainer(this.paLevel, pair);
		final Effect<P2<IJavaElement, IJavaElement>> calculateSubchange = 
				new Effect<P2<IJavaElement, IJavaElement>>() {
				@Override
				public void e(P2<IJavaElement, IJavaElement> p) {
					change.addSubChange(cuCalculator.CalculateJavaModelChange(
						new JavaElementPair(p._1(), p._2())));
		}};
		
		JavaModelAnalyzer.getSameNameElementPairsFunction().f(JavaModelAnalyzer.
			getICompilationUnit(pair.getElementBefore()), JavaModelAnalyzer.
				getICompilationUnit(pair.getElementAfter())).foreach(calculateSubchange);
		
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
				int score = (int) (DlfStringUtils.getSamePartPercentage.f(n1, n2) * 10);
				logger.debug("Name similarity score: " + score);
				return score;
		}});
		
		similarPairs.foreach(calculateSubchange);
		removedUnits = removedUnits.minus(eq, similarPairs.map(getFirst));
		addedUnits = addedUnits.minus(eq, similarPairs.map(getSecond));
		removedUnits.map(FJUtils.appendElementFunc((IJavaElement)null, 
			(IJavaElement)null)).foreach(calculateSubchange);
		addedUnits.map(FJUtils.prependElementFunc((IJavaElement)null, 
			(IJavaElement)null)).foreach(calculateSubchange);
		return change;
	}

}
