package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.IJavaElement;

public interface IJavaElementPair {
	IJavaElement getElementBefore();
	IJavaElement getElementAfter();
}
