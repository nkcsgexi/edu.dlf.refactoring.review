package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.IJavaElement;

public class JavaElementPair {
	
	private final IJavaElement beforeElement;
	private final IJavaElement afterElement;

	public JavaElementPair(IJavaElement beforeElement, IJavaElement afterElement)
	{
		this.beforeElement = beforeElement;
		this.afterElement = afterElement;
	};
	
	public IJavaElement GetBeforeElement()
	{
		return this.beforeElement;
	}
	
	public IJavaElement GetAfterElement()
	{
		return this.afterElement;
	}
}
