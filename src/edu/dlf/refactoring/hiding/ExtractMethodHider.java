package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;

public class ExtractMethodHider extends AbstractRefactoringHider{
	private final Logger logger;
	private final F<ASTNode, List<ASTNode>> findMethodsDecFunc = ASTAnalyzer.
		getDecendantFunc().f(ASTNode.METHOD_DECLARATION);
	private final F<ASTNode, List<ASTNode>> findMethodInvsFunc = ASTAnalyzer.
		getDecendantFunc().f(ASTNode.METHOD_INVOCATION);
	
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
		final String statements = ASTNode2ASTNodeUtils.getMethodStatements.f(addedMethod).
			map(astNode2String).foldLeft(XStringUtils.stringCombiner, 
				new StringBuilder()).toString();
		
		ASTUpdator updator = new ASTUpdator();
		findMethodsDecFunc.f(afterRoot).filter(ASTAnalyzer.
			getMethodDeclarationNamesEqualFunc().f(addedMethod)).map(
				new F<ASTNode, P2<ASTNode, String>>() {
					@Override
					public P2<ASTNode, String> f(ASTNode node) {
						return P.p(node, "");
		}}).foreach(add2Updator.f(updator));
		
		findMethodInvsFunc.f(afterRoot).filter(new F<ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode inv) {
				return ASTAnalyzer.getInvokedMethodName.f(inv).equals(addedMethodName);
		}}).map(ASTNode2ASTNodeUtils.getEnclosingStatement).map
			(new F<ASTNode, P2<ASTNode, String>>() {
			@Override
			public P2<ASTNode, String> f(ASTNode node) {
				return P.p(node, statements);
		}}).foreach(add2Updator.f(updator));
	
		return updator.f(afterRoot);
	}
}
