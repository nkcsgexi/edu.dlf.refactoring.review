package edu.dlf.refactoring.change.calculator;

import org.apache.commons.io.FilenameUtils;
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
	
	private final F2<IJavaElement, IJavaElement, Integer> similarityFunc = FJUtils.
		getCommonWordsStringSimilarityFunc(100, JavaModelAnalyzer.
			getElementNameFunc);
		
	private final F2<IJavaElement, IJavaElement, Integer> refinedSimilarityFunc
		= new F2<IJavaElement, IJavaElement, Integer>() {
			@Override
			public Integer f(IJavaElement ele0, IJavaElement ele1) {
				String name0 =  FilenameUtils.removeExtension(ele0.getElementName());
				String name1 = FilenameUtils.removeExtension(ele1.getElementName());
				String longName = name0.length() > name1.length() ? name0 : name1;
				String shortName = name0 == longName ? name1 : name0;
				if(shortName.length() != longName.length()) {
					String rest = longName.substring(shortName.length());
					if(rest.toLowerCase().startsWith("test")) {
						return Integer.MIN_VALUE;
					}
				}
				return similarityFunc.f(ele0, ele1);
	}};
	
	@Override
	public ISourceChange calculate(JavaElementPair pair) {
		logger.debug("Compare packages: " + pair.getElementBefore().
			getElementName() + ":" + pair.getElementAfter().getElementName());
		final SubChangeContainer change = new SubChangeContainer(this.paLevel, pair);
		final Effect<P2<IJavaElement, IJavaElement>> calculateSubchange = 
				new Effect<P2<IJavaElement, IJavaElement>>() {
				@Override
				public void e(P2<IJavaElement, IJavaElement> p) {
					change.addSubChange(cuCalculator.calculate(
						new JavaElementPair(p._1(), p._2())));
		}};
		List<IJavaElement> unitsAfter = JavaModelAnalyzer.getICompilationUnitFunc.f
			(pair.getElementBefore()); 
		List<IJavaElement> unitsBefore = JavaModelAnalyzer.getICompilationUnitFunc.f
			(pair.getElementAfter());
		F2<List<IJavaElement>, List<IJavaElement>, List<P2<IJavaElement, 
			IJavaElement>>> mapper = FJUtils.getSimilarityMapper(20, 
				refinedSimilarityFunc);
		mapper.f(unitsBefore, unitsAfter).foreach(calculateSubchange);
		return change;
	}

}
