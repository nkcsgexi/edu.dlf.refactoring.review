package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementPair implements IJavaElementPair{
	
	private final IJavaElement beforeElement;
	private final IJavaElement afterElement;

	public JavaElementPair(IJavaElement beforeElement, IJavaElement afterElement)
	{
		this.beforeElement = beforeElement;
		this.afterElement = afterElement;
	}
	
	public IJavaElement getElementBefore()
	{
		return this.beforeElement;
	}
	
	public IJavaElement getElementAfter()
	{
		return this.afterElement;
	}
}
