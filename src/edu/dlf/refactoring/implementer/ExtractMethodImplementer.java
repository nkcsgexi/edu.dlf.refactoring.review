package edu.dlf.refactoring.implementer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;

public class ExtractMethodImplementer implements IRefactoringImplementer{

	@Override
	public IImplementedRefactoring implementRefactoring(IDetectedRefactoring 
			detectedRefactoring) {
		List<ASTNode> statements = detectedRefactoring.getEffectedNodeList
				(DetectedExtractMethodRefactoring.ExtractedStatements);
		statements = getLongestSequentialNodes(statements);
		Ord<ASTNode> ord = Ord.intOrd.comap(new F<ASTNode, Integer>(){
			@Override
			public Integer f(ASTNode node) {
				return node.getStartPosition();
		}});
		
		int start = statements.minimum(ord).getStartPosition();
		ASTNode last = statements.maximum(ord);
		int end = last.getStartPosition() + last.getLength();
		CompilationUnit unit = (CompilationUnit) statements.head().getRoot();
		ExtractMethodRefactoring refactoring  = new ExtractMethodRefactoring
				(unit, start, end - start);
		
		return null;
	}
	
	
	private List<ASTNode> getLongestSequentialNodes(List<ASTNode> statements) {
		return statements.zip(statements.drop(1).snoc(null)).span(new 
				F<P2<ASTNode, ASTNode>, Boolean>(){
			@Override
			public Boolean f(P2<ASTNode, ASTNode> pair) {
				if(pair._2() == null) return true;
				return ASTAnalyzer.AreASTNodeNeighbors(pair._1(), pair._2());
			}})._1().map(new F<P2<ASTNode, ASTNode>, ASTNode>(){
				@Override
				public ASTNode f(P2<ASTNode, ASTNode> arg0) {
					return arg0._1();
				}});
	}

}