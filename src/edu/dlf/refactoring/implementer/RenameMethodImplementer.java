package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.refactorings.DetectedRenameMethodRefactoring;
import edu.dlf.refactoring.utils.RefactoringUtils;
import fj.data.List;
import fj.data.Option;

public class RenameMethodImplementer extends AbstractRenameImplementer{

	private final Logger logger;
	private final IASTNodeChangeCalculator cuCalculator;

	@Inject
	public RenameMethodImplementer(
		Logger logger,
		@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculater) {
		super(logger, cuCalculater);
		this.logger = logger;
		this.cuCalculator = cuCalculater;
	}

	@Override
	public void implementRefactoring
			(final IDetectedRefactoring detectedRefactoring, 
			final IImplementedRefactoringCallback callback) {
		List<ASTNode> names = detectedRefactoring.getEffectedNodeList(
			DetectedRenameMethodRefactoring.SimpleNamesBefore);
		List<IJavaElement> elements = names.map(resolveSimpleNameElement).
			nub(JavaModelAnalyzer.getJavaElementEQ());
		IJavaElement declaration = elements.head();
		JavaRenameProcessor processor = this.getRenameProcessor(declaration);
		processor.setNewElementName(getNewName.f(DetectedRenameMethodRefactoring.
			SimpleNamesAfter, detectedRefactoring));
		RenameRefactoring autoRefactoring = this.getRenameRefactoring(processor);
		Option<Change> op = RefactoringUtils.createChange(autoRefactoring);
		if(op.isNone()) return;
		collectAutoRefactoringChangesAsync(op.some(), 
			new IAutoChangeCallback() {
			@Override
			public void onFinishChanges(List<ISourceChange> 
				changes) {
				IImplementedRefactoring implemented = new 
					ImplementedRefactoring(RefactoringType.RenameMethod, 
						changes);
				callback.onImplementedRefactoringReady(detectedRefactoring, 
					implemented);
		}});
	}


}
