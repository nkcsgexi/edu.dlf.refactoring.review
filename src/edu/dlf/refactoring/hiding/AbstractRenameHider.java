package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import fj.F;
import fj.P;
import fj.P2;

public abstract class AbstractRenameHider extends AbstractRefactoringHider{

	private final Logger logger;

	protected abstract NodeListDescriptor getBeforeNamesDescriptor();
	protected abstract NodeListDescriptor getAfterNamesDescriptor();
	
	protected AbstractRenameHider(Logger logger)
	{
		this.logger = logger;
	}
	
	@Override
	public ASTNode f(IDetectedRefactoring refactoring, ASTNode rootAfter) {
		final String beforeName = refactoring.getEffectedNodeList
			(getBeforeNamesDescriptor()).head().toString();
		final String afterName = refactoring.getEffectedNodeList
			(getAfterNamesDescriptor()).head().toString();
		UpdateRuleBuilder builder = new UpdateRuleBuilder();
		builder.addUpdateRule(new F<ASTNode, P2<String,Boolean>>() {
			@Override
			public P2<String, Boolean> f(ASTNode node) {
				if(node.getNodeType() == ASTNode.SIMPLE_NAME
					&& node.toString().equals(afterName))
				{	
					return P.p(beforeName, true);
				}
				return getDefaultUpdate.f(node);
		}});
		return new ASTUpdator(builder.getCombinedRule()).f(rootAfter);
	}

}
