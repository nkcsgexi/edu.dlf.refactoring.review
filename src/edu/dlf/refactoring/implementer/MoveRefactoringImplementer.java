package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IConfirmQuery;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgPolicy.IMovePolicy;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgQueries;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgDestinationFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgUtils;
import org.eclipse.jdt.internal.corext.refactoring.structure.MoveStaticMembersProcessor;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;

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
	public MoveRefactoringImplementer(
		Logger logger,
		@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculator)
	{
		this.logger = logger;
		this.cuCalculator = cuCalculator;
	}
	
	@Override
	public Option<IImplementedRefactoring> implementRefactoring(
			IDetectedRefactoring detectedRefactoring) {
		ASTNode removedDec = detectedRefactoring.getEffectedNode(DetectedMoveRefactoring.
			RemovedDeclarationDescriptor);
		ASTNode addedDec = detectedRefactoring.getEffectedNode(DetectedMoveRefactoring.
			AddedDeclarationDescripter);		
		List<IJavaElement> elements = JavaModelAnalyzer.getOverlapMembers(removedDec);
		IJavaElement dest = JavaModelAnalyzer.getAssociatedITypes(addedDec).head();
		try{
			if(elements.length() == 1)
			{
				Refactoring refactoring = createMoveStaticMemberRefactoring(
					elements.head(), dest);
				if(refactoring != null)
				{
					Option<Change> change = RefactoringUtils.createChange(refactoring);
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
			}
			else
				logger.fatal("Elements count is " + elements.length());
		}catch (Exception e)
		{
			logger.fatal(e);
		}
		return Option.none();	
	}
		
	
	private Refactoring createMoveStaticMemberRefactoring(IJavaElement target,
		IJavaElement destinationType)
	{
		try{
			IJavaElement project = target.getJavaProject();
			MoveStaticMembersProcessor processor = new MoveStaticMembersProcessor
				(new IMember[]{(IMember) target}, JavaPreferencesSettings.
				getCodeGenerationSettings((IJavaProject) project));
			processor.setDestinationTypeFullyQualifiedName(JavaModelAnalyzer.
				getQualifiedTypeName(destinationType));
			return new MoveRefactoring(processor);
		}catch(Exception e)
		{
			logger.fatal(e);
			return null;
		}
	}
	
	
	private Refactoring createMoveRefactoring(final IJavaElement element, final 
		IJavaElement destination)
	{  
		try{
			if(element == null)
				throw new Exception("Element to move is null.");
			if(destination == null)
				throw new Exception("Destination is null.");
			IResource[] resources = ReorgUtils.getResources(new IJavaElement
				[]{element});
			IMovePolicy policy = ReorgPolicyFactory.createMovePolicy(resources, 
				new IJavaElement[]{element});
			policy.setDestinationCheck(true);
			policy.setUpdateReferences(true);
			policy.setDestination(ReorgDestinationFactory.createDestination
				(destination));
			JavaMoveProcessor processor = new JavaMoveProcessor(policy);
			processor.setReorgQueries(new MockReorgQueries());
			return new MoveRefactoring(processor);
		}catch(Exception e)
		{
			logger.fatal("Cannot create refactoring: " + e);
			return null;
		}
	}
	
	private class MockReorgQueries implements IReorgQueries {
		private final java.util.List<Integer> fQueriesRun= new java.util.
			ArrayList<Integer>();

		public IConfirmQuery createYesNoQuery(String queryTitle, boolean 
			allowCancel, int queryID) {
			run(queryID);
			return yesQuery;
		}

		public IConfirmQuery createYesYesToAllNoNoToAllQuery(String queryTitle, 
			boolean allowCancel, int queryID) {
			run(queryID);
			return yesQuery;
		}

		private void run(int queryID) {
			fQueriesRun.add(new Integer(queryID));
		}
		
		private final IConfirmQuery yesQuery= new IConfirmQuery() {
			public boolean confirm(String question) throws 
				OperationCanceledException {
				return true;
			}

			public boolean confirm(String question, Object[] elements) throws 
				OperationCanceledException {
				return true;
			}
		};

		public IConfirmQuery createSkipQuery(String queryTitle, int queryID) {
			run(queryID);
			return yesQuery;
		}
	}
}
