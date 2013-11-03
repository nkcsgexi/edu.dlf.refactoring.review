package edu.dlf.refactoring.hiding;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.Option;

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
		
	protected class UpdateRuleBuilder
	{
		private List<F<ASTNode, P2<String, Boolean>>> rules = List.nil();
		
		public void addUpdateRule(F<ASTNode, P2<String, Boolean>> rule)
		{
			this.rules = this.rules.snoc(rule);
		}
		
		public F<ASTNode,P2<String,Boolean>> getCombinedRule()
		{
			final List<F<ASTNode, P2<String, Boolean>>> list = rules;
			return new F<ASTNode, P2<String,Boolean>>() {
				@Override
				public P2<String, Boolean> f(final ASTNode node) {
					List<P2<String, Boolean>> results = list.map(new F<F<ASTNode, 
						P2<String,Boolean>>, P2<String,Boolean>>() {
						@Override
						public P2<String, Boolean> f(
								F<ASTNode, P2<String, Boolean>> rule) {
							return rule.f(node);
						}});
					Option<P2<String, Boolean>> update = results.find(
						new F<P2<String,Boolean>, Boolean>() {
						@Override
						public Boolean f(P2<String, Boolean> p) {
							return p._2();
						}});
					return update.isSome() ? update.some() : P.p(node.toString(), 
						false);
				}};
		}
	}
}
