package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgPolicy.IMovePolicy;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgDestinationFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgUtils;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.refactorings.DetectedMoveRefactoring;
import edu.dlf.refactoring.utils.RefactoringUtils;
import fj.data.List;
import fj.data.Option;

public class MoveRefactoringImplementer implements IRefactoringImplementer{

	private final Logger logger;
	private final IASTNodeChangeCalculator cuCalculator;

	@Inject
	public MoveRefactoringImplementer(Logger logger,
			@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculator)
	{
		this.logger = logger;
		this.cuCalculator = cuCalculator;
	}
	
	@Override
	public Option<IImplementedRefactoring> implementRefactoring(
			IDetectedRefactoring refactoring) {
		ASTNode removedDec = refactoring.getEffectedNode(DetectedMoveRefactoring.
			RemovedDeclarationDescriptor);
		ASTNode addedDec = refactoring.getEffectedNode(DetectedMoveRefactoring.
			AddedDeclarationDescripter);		
		List<IJavaElement> elements = JavaModelAnalyzer.getOverlapElements(removedDec);
		IJavaElement dest = JavaModelAnalyzer.getAssociatedICompilationUnit(addedDec);
		try{
			if(elements.length() != 1)
			{
				Refactoring ref = createMoveRefactoring(elements.head(), dest);
				Option<Change> change = RefactoringUtils.createChange(ref);
				if(change.isSome())
				{
					List<ASTNodePair> pairs = RefactoringUtils.
						collectChangedCompilationUnits(change.some());
					List<ISourceChange> changes = SourceChangeUtils.
						calculateASTNodeChanges(pairs, cuCalculator);
					if(changes.isNotEmpty()) logger.info("Move auto-performed.");
					return Option.some((IImplementedRefactoring)new 
						ImplementedRefactoring(RefactoringType.Move, changes));
				}
			}
			throw new Exception();
		}catch (Exception e)
		{
			logger.fatal(e);
			return Option.none();
		}
	}
		
	
	private Refactoring createMoveRefactoring(final IJavaElement element, final 
		IJavaElement destination) throws Exception
	{
		IResource[] resources = ReorgUtils.getResources(new IJavaElement
			[]{element});
		IMovePolicy policy = ReorgPolicyFactory.createMovePolicy(resources, 
			new IJavaElement[]{element});
		policy.setDestinationCheck(true);
		policy.setUpdateReferences(true);
		policy.setUpdateReferences(true);
		policy.setDestination(ReorgDestinationFactory.createDestination(destination));
		return new MoveRefactoring(new JavaMoveProcessor(policy));	
	}
}
