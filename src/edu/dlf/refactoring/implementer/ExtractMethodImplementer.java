package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.ltk.core.refactoring.Change;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import edu.dlf.refactoring.utils.RefactoringUtils;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.Option;

public class ExtractMethodImplementer implements IRefactoringImplementer{
	
	private final Logger logger;
	private final IASTNodeChangeCalculator cuCalculator;

	@Inject
	public ExtractMethodImplementer(
			Logger logger,
			@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculator)
	{
		this.logger = logger;
		this.cuCalculator = cuCalculator;
	}
	
	@Override
	public Option<IImplementedRefactoring> implementRefactoring(IDetectedRefactoring 
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
		Option<Change> change = RefactoringUtils.createChange(refactoring);
		if(change.isSome())
		{
			List<ISourceChange> sourceChanges = RefactoringUtils.
				collectChangedCompilationUnits(change.some()).map(
					new F<ASTNodePair, ISourceChange>(){
					@Override
					public ISourceChange f(ASTNodePair pair) {
						return SourceChangeUtils.pruneSourceChange(
							cuCalculator.CalculateASTNodeChange(pair));
					}});
			return Option.some((IImplementedRefactoring) new 
				ImplementedRefactoring(RefactoringType.ExtractMethod, 
					sourceChanges));
		}
		return Option.none();
	}	
	
	
	private List<ASTNode> getLongestSequentialNodes(List<ASTNode> statements) {
		final ASTNode head = statements.head();
		List<ASTNode> statements2 = statements.drop(1).snoc(head);
		return statements.zip(statements2).span(new F<P2<ASTNode, ASTNode>, 
				Boolean>(){
			@Override
			public Boolean f(P2<ASTNode, ASTNode> pair) {
				if(pair._2() == head) return true;
				return ASTAnalyzer.areNodesNeighbors(pair._1(), pair._2());
			}})._1().map(new F<P2<ASTNode,ASTNode>, ASTNode>(){
				@Override
				public ASTNode f(P2<ASTNode, ASTNode> pair) {
					return pair._1();
				}});
	}

}
