package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.corext.refactoring.code.ExtractMethodRefactoring;
import org.eclipse.ltk.core.refactoring.Change;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2Boolean;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.refactorings.DetectedExtractMethodRefactoring;
import edu.dlf.refactoring.utils.RefactoringUtils;
import fj.F;
import fj.Ord;
import fj.P2;
import fj.data.List;
import fj.data.Option;

public class ExtractMethodImplementer extends AbstractRefactoringImplementer{
	
	private final Logger logger;

	@Inject
	public ExtractMethodImplementer(
			Logger logger,
			@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculator) {
		super(logger, cuCalculator);
		this.logger = logger;
	}
	
	@Override
	public synchronized void implementRefactoring
		(final IDetectedRefactoring detectedRefactoring, 
			final IImplementedRefactoringCallback callback) {
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
		if(change.isSome()) {
			collectAutoRefactoringChangesAsync(change.some(), 
				new IAutoChangeCallback() {
				@Override
				public void onFinishChanges(List<ISourceChange> 
					changes) {
					IImplementedRefactoring implemented = new 
						ImplementedRefactoring(RefactoringType.ExtractMethod, 
							changes);
					callback.onImplementedRefactoringReady(detectedRefactoring, 
						implemented);
		}});}
	}
	
	private List<ASTNode> getLongestSequentialNodes(List<ASTNode> statements) {
		final ASTNode head = statements.head();
		List<ASTNode> statements2 = statements.drop(1).snoc(head);
		return statements.zip(statements2).span(new F<P2<ASTNode, ASTNode>, 
				Boolean>(){
			@Override
			public Boolean f(P2<ASTNode, ASTNode> pair) {
				if(pair._2() == head) return true;
				return ASTNode2Boolean.areASTNodesNeighbors.f(pair._1(), pair._2());
			}})._1().map(new F<P2<ASTNode,ASTNode>, ASTNode>(){
				@Override
				public ASTNode f(P2<ASTNode, ASTNode> pair) {
					return pair._1();
				}});
	}

}
