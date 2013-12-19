package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import fj.Effect;
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
			Logger logger) {
		this.paLevel = paLevel;
		this.cuCalculator = cuCalculator;
		this.logger = logger;
	}
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		logger.debug("Compare packages: " + pair.getElementBefore().
			getElementName() + ":" + pair.getElementAfter().getElementName());
		final SubChangeContainer change = new SubChangeContainer(this.paLevel, pair);
		final Effect<P2<IJavaElement, IJavaElement>> calculateSubchange = 
				new Effect<P2<IJavaElement, IJavaElement>>() {
				@Override
				public void e(P2<IJavaElement, IJavaElement> p) {
					change.addSubChange(cuCalculator.CalculateJavaModelChange(
						new JavaElementPair(p._1(), p._2())));
		}};
		List<IJavaElement> unitsAfter = JavaModelAnalyzer.getICompilationUnit
			(pair.getElementBefore()); 
		List<IJavaElement> unitsBefore = JavaModelAnalyzer.getICompilationUnit
			(pair.getElementAfter());
		F2<IJavaElement, IJavaElement, Integer> similarityFunc = FJUtils.
			getCommonWordsStringSimilarityFunc(100, JavaModelAnalyzer.
				getElementNameFunc);
		F2<List<IJavaElement>, List<IJavaElement>, List<P2<IJavaElement, 
			IJavaElement>>> mapper = FJUtils.getSimilarityMapper(80, 
				similarityFunc);
		mapper.f(unitsBefore, unitsAfter).foreach(calculateSubchange);
		return change;
	}

}
