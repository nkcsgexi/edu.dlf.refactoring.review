package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;

public class ExtractMethodHider extends AbstractRefactoringHider{

	private final Logger logger;
	
	@Inject
	public ExtractMethodHider(Logger logger)
	{
		this.logger = logger;
	}
	
	@Override
	public ASTNode f(IDetectedRefactoring refactoring, ASTNode afterRoot) {
		final ASTNode addedMethod = refactoring.getEffectedNode(
			DetectedExtractMethodRefactoring.DeclaredMethod);
		final String addedMethodName = addedMethod.getStructuralProperty
			(MethodDeclaration.NAME_PROPERTY).toString();
		final List<ASTNode> removedStatements = refactoring.getEffectedNodeList(
			DetectedExtractMethodRefactoring.ExtractedStatements);
		final String statements = removedStatements.map(astNode2String).foldLeft
			(XStringUtils.stringCombiner, new StringBuilder()).toString();
		
		UpdateRuleBuilder builder = new UpdateRuleBuilder();
		
		// Add remove method rule.
		builder.addUpdateRule(new F<ASTNode, P2<String,Boolean>>() {
			@Override
			public P2<String, Boolean> f(ASTNode node) {
				if(node.getNodeType() == ASTNode.METHOD_DECLARATION) {
					if(ASTAnalyzer.getMethodDeclarationNamesEqualFunc().f
						(addedMethod, node))
						return P.p("", true);
				}
				return getDefaultUpdate.f(node);
			}});
		
		// Add flatten method invocation
		builder.addUpdateRule(new F<ASTNode, P2<String,Boolean>>() {
			@Override
			public P2<String, Boolean> f(ASTNode node) {
				if(node.getNodeType() == ASTNode.METHOD_INVOCATION)
				{
					String name = ASTAnalyzer.getInvokedMethodName.f(node);
					if(name.equals(addedMethodName))
					{
						return P.p(statements, true);
					}
				}
				return getDefaultUpdate.f(node);
		}});		
		return new ASTUpdator(builder.getCombinedRule()).f(afterRoot);
	}
}
