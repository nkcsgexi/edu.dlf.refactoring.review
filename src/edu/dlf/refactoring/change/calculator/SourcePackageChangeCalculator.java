package edu.dlf.refactoring.change.calculator;

import org.eclipse.jdt.core.IJavaElement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SourcePackageAnnotation;
import edu.dlf.refactoring.change.IJavaModelChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;
import fj.Effect;
import fj.P2;
import fj.data.List;

public class SourcePackageChangeCalculator implements IJavaModelChangeCalculator{

	private final IJavaModelChangeCalculator cuCalculator;
	private final String paLevel;

	@Inject
	public SourcePackageChangeCalculator(
			@SourcePackageAnnotation String paLevel,
			@CompilationUnitAnnotation IJavaModelChangeCalculator cuCalculator)
	{
		this.paLevel = paLevel;
		this.cuCalculator = cuCalculator;
	}
	
	@Override
	public ISourceChange CalculateJavaModelChange(JavaElementPair pair) {
		final SubChangeContainer change = new SubChangeContainer(this.paLevel, pair);
		JavaModelAnalyzer.getSameNameElementPairsFunction().f(JavaModelAnalyzer.
			getICompilationUnit(pair.getElementBefore()), JavaModelAnalyzer.
				getICompilationUnit(pair.getElementAfter())).foreach(
					new Effect<P2<IJavaElement, IJavaElement>>() {
					@Override
					public void e(P2<IJavaElement, IJavaElement> p) {
						change.addSubChange(cuCalculator.CalculateJavaModelChange
							(new JavaElementPair(p._1(), p._2())));
					}
				});
		List<IJavaElement> addedUnits = JavaModelAnalyzer.getAddedElementsFunction().
			f(JavaModelAnalyzer.getICompilationUnit(pair.getElementBefore()), JavaModelAnalyzer.
				getICompilationUnit(pair.getElementAfter()));
		List<IJavaElement> removedUnits = JavaModelAnalyzer.getRemovedElementsFunction().
			f(JavaModelAnalyzer.getICompilationUnit(pair.getElementBefore()), JavaModelAnalyzer.
				getICompilationUnit(pair.getElementAfter()));
			
		
		return change;
	}

}
