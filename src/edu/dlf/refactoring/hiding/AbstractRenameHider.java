package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import fj.Effect;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;

public abstract class AbstractRenameHider extends AbstractRefactoringHider{

	private final Logger logger;

	protected abstract NodeListDescriptor getBeforeNamesDescriptor();
	protected abstract NodeListDescriptor getAfterNamesDescriptor();
	protected abstract boolean isSimpleNameToUpdate(ASTNode name);
	
	
	private F<ASTNode, List<ASTNode>> getSimpleNameFunc = ASTAnalyzer.getDecendantFunc().
		f(ASTNode.SIMPLE_NAME);
	
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
		ASTUpdator updator = new ASTUpdator();
		Effect<P2<ASTNode, String>> addFunc = add2Updator.f(updator);
		getSimpleNameFunc.f(rootAfter).filter(new F<ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode name) {
				return name.toString().equals(afterName) && isSimpleNameToUpdate(name);
		}}).map(new F<ASTNode, P2<ASTNode, String>>() {
			@Override
			public P2<ASTNode, String> f(ASTNode name) {
				return P.p(name, beforeName);
			}}).foreach(addFunc);
		return updator.f(rootAfter);
	}

}
