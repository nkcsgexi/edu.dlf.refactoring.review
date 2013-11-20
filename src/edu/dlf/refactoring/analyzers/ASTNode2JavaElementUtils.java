package edu.dlf.refactoring.analyzers;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Name;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;

public class ASTNode2JavaElementUtils {

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private ASTNode2JavaElementUtils() throws Exception {
		throw new Exception();
	}
	
	public static final F<ASTNode, IJavaElement> getDeclarationElement = 
		new F<ASTNode, IJavaElement>() {
			@Override
			public IJavaElement f(ASTNode name) {
				if(!(name instanceof Name)) logger.fatal("Should be a name.");
				return ((Name)name).resolveBinding().getJavaElement();
	}}; 
}
