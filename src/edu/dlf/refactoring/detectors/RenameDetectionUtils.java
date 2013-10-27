package edu.dlf.refactoring.detectors;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.dlf.refactoring.design.ISourceChange;
import fj.F;

public class RenameDetectionUtils {

	private RenameDetectionUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static F<ASTNode, String> resolveBindingKeyFunc()
	{
		return new F<ASTNode, String>() {
			@Override
			public String f(ASTNode name) {
				SimpleName sn = (SimpleName) name;
				return sn.resolveBinding().getKey();
		}};
	}
	
	public static F<ISourceChange, String> getBeforeAndAfterKeyFunc()
	{
		return new F<ISourceChange, String>() {
			@Override
			public String f(ISourceChange change) {
				return resolveBindingKeyFunc().f(change.getNodeBefore()) +
					resolveBindingKeyFunc().f(change.getNodeAfter());
			}
		};
	}

	
}
