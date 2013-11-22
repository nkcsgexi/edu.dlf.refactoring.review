package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2JavaElementUtils;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.refactorings.DetectedRenameField;
import edu.dlf.refactoring.utils.RefactoringUtils;
import fj.data.List;
import fj.data.Option;

public class RenameFieldImplementer extends AbstractRenameImplementer{

	private final Logger logger;

	@Inject
	public RenameFieldImplementer(Logger logger,
		@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculater) {
		super(logger, cuCalculater);
		this.logger = logger;
	}

	@Override
	public void implementRefactoring(final IDetectedRefactoring detectedRefactoring,
		final IImplementedRefactoringCallback callback) {
		List<ASTNode> namesBefore = detectedRefactoring.getEffectedNodeList(DetectedRenameField.
			SimpleNamesBefore);
		IJavaElement element = ASTNode2JavaElementUtils.getDeclarationElement.
			f(namesBefore.head());
		JavaRenameProcessor processor = getRenameProcessor(element);
		String newName = getNewName.f(DetectedRenameField.SimpleNamesAfter, 
			detectedRefactoring);
		processor.setNewElementName(newName);
		RenameRefactoring refactoring = getRenameRefactoring(processor);
		Option<Change> changeOp = RefactoringUtils.createChange(refactoring);
		if(changeOp.isSome()) {
			this.collectAutoRefactoringChangesAsync(changeOp.some(), 
				new IAutoChangeCallback() {
				@Override
				public void onFinishChanges(List<ISourceChange> cuChanges) {
					logger.info("Auto rename field collected.");
					callback.onImplementedRefactoringReady(detectedRefactoring, 
						new ImplementedRefactoring(RefactoringType.RenameField, 
							cuChanges));
			}});
		}
	}
}
