package edu.dlf.refactoring.analyzers;

import org.eclipse.jdt.core.dom.ASTNode;

import fj.F;
import fj.F2;

public class ASTNode2Boolean {
	private ASTNode2Boolean() throws Exception {
		throw new Exception();
	}
	
	public static F2<ASTNode, ASTNode, Boolean> isFirstAncestorOfSecond =
		new F2<ASTNode, ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode first, ASTNode second) {
				F<ASTNode, Boolean> finder = FJUtils.getReferenceEq
					((ASTNode)null).eq(second);
				return ASTAnalyzer.getAllDecendantsFunc.f(first).find(finder).
					isSome();
	}};
}
