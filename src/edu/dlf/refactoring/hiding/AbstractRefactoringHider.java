package edu.dlf.refactoring.hiding;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import fj.Effect;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;

public abstract class AbstractRefactoringHider extends F2<IDetectedRefactoring, 
	ASTNode, ASTNode>{

	protected F<ASTNode, String> astNode2String = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			return node.toString();
		}};
		
		
	protected F<ASTNode, P2<String, Boolean>> getDefaultUpdate = 
		new F<ASTNode, P2<String,Boolean>>() {
		@Override
		public P2<String, Boolean> f(ASTNode node) {
			return P.p(node.toString(), false);
	}}; 
	
	
	protected F<ASTUpdator, Effect<P2<ASTNode, String>>> add2Updator = 
		new F<ASTUpdator, Effect<P2<ASTNode,String>>>() {
		@Override
		public Effect<P2<ASTNode, String>> f(final ASTUpdator up) {
			return new Effect<P2<ASTNode,String>>() {
				@Override
				public void e(P2<ASTNode, String> pair) {
					up.addNodeUpdate(pair._1(), pair._2());
	}};}};
}
