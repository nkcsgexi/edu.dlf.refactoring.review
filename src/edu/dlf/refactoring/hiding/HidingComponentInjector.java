package edu.dlf.refactoring.hiding;

import com.google.inject.AbstractModule;

import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameLocalVariable;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;

public class HidingComponentInjector extends AbstractModule{
	
	@Override
	protected void configure() {
		bind(AbstractRefactoringHider.class).annotatedWith(ExtractMethod.class).to(ExtractMethodHider.class);
		bind(AbstractRefactoringHider.class).annotatedWith(RenameMethod.class).to(RenameMethodHider.class);
		bind(AbstractRefactoringHider.class).annotatedWith(RenameType.class).to(RenameTypeHider.class);
		bind(AbstractRefactoringHider.class).annotatedWith(RenameLocalVariable.class).to(RenameLocalVariableHider.class);
		bind(AbstractRefactoringHider.class).annotatedWith(MoveResource.class).to(MoveResourceHider.class);
	}

}
