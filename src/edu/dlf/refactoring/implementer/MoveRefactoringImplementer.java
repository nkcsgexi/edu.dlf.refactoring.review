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
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.refactorings.DetectedMoveRefactoring;
import fj.data.Option;

public class MoveRefactoringImplementer implements IRefactoringImplementer{

	private final Logger logger;

	@Inject
	public MoveRefactoringImplementer(Logger logger)
	{
		this.logger = logger;
	}
	
	@Override
	public Option<IImplementedRefactoring> implementRefactoring(
			IDetectedRefactoring refactoring) {
		ASTNode removedDec = refactoring.getEffectedNode(DetectedMoveRefactoring.
				RemovedDeclarationDescriptor);
		JavaModelAnalyzer.getOverlapElements(removedDec);
		return null;
	}
	
	
	private Refactoring createMoveProcessor(final IJavaElement element, final 
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
