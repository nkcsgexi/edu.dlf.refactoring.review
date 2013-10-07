package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.refactorings.RenameMethodRefactoring;
import edu.dlf.refactoring.utils.RefactoringUtils;
import fj.Equal;
import fj.F;
import fj.data.List;
import fj.data.Option;

public class RenameMethodImplementer extends AbstractRenameImplementer{

	private final Logger logger;
	private final IASTNodeChangeCalculator cuCalculator;

	@Inject
	public RenameMethodImplementer(
		Logger logger,
		@CompilationUnitAnnotation IASTNodeChangeCalculator cuCalculater)
	{
		super(logger);
		this.logger = logger;
		this.cuCalculator = cuCalculater;
	}

	@Override
	public Option<IImplementedRefactoring> implementRefactoring
		(IDetectedRefactoring refactoring) {
		List<ASTNode> names = refactoring.getEffectedNodeList(RenameMethodRefactoring.
			SimpleNamesBefore);
		F<ASTNode, IJavaElement> getElement = new F<ASTNode, IJavaElement>() {
			@Override
			public IJavaElement f(ASTNode node) {
				SimpleName name = (SimpleName) node;
				return name.resolveBinding().getJavaElement();
			}
		};
		Equal<IJavaElement> eq = Equal.equal(new F<IJavaElement, F<IJavaElement, 
			Boolean>>(){
			@Override
			public F<IJavaElement, Boolean> f(final IJavaElement ele1) {
				return new F<IJavaElement, Boolean>() {
					@Override
					public Boolean f(final IJavaElement ele2) {
						return ele1 == ele2;
					}
				};
			}});
		List<IJavaElement> elements = names.map(getElement).nub(eq);
		if(elements.length() > 1) logger.fatal("Renamed methods are multiple.");
		IJavaElement declaration = elements.head();
		JavaRenameProcessor processor = this.getRenameProcessor(declaration);
		processor.setNewElementName("Name");
		RenameRefactoring autoRefactoring = this.getRenameRefactoring(processor);
		Option<Change> op = RefactoringUtils.createChange(autoRefactoring);
		if(op.isNone()) return Option.none();
		List<ISourceChange> sourceChanges = RefactoringUtils.
			collectChangedCompilationUnits(op.some()).map(
				new F<ASTNodePair, ISourceChange>() {
					@Override
					public ISourceChange f(ASTNodePair pair) {
						return cuCalculator.CalculateASTNodeChange(pair);
					}
				}).map(SourceChangeUtils.getPruneSourceChangeFunc());
		return Option.some((IImplementedRefactoring)new ImplementedRefactoring
				(RefactoringType.RenameMethod, sourceChanges));
	}

}
