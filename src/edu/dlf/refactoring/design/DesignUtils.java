package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.IJavaElement;

import fj.F;
import fj.F2;
import fj.P2;
import fj.Unit;

public class DesignUtils {
	
	private DesignUtils() throws Exception{
		throw new Exception();
	}
	
	public static final F<P2<IJavaElement, IJavaElement>, JavaElementPair> 
		convertProduct2JavaElementPair = new F<P2<IJavaElement,IJavaElement>, 
			JavaElementPair>() {
			@Override
			public JavaElementPair f(P2<IJavaElement, IJavaElement> pair) {
				return new JavaElementPair(pair._1(), pair._2());
	}};
	
	public static final F2<Object, IFactorComponent, Unit> feedComponent = 
		new F2<Object, IFactorComponent, Unit>() {
			@Override
			public Unit f(Object ob, IFactorComponent comp) {
				comp.listen(ob);
				return Unit.unit();
	}}; 
}
