package edu.dlf.refactoring.implementer;

import com.google.inject.AbstractModule;

import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;

public class ImplementerCompInjector extends AbstractModule{

	@Override
	protected void configure() {
		bind(IRefactoringImplementer.class).annotatedWith(ExtractMethod.class).to(ExtractMethodImplementer.class);
		bind(IRefactoringImplementer.class).annotatedWith(RenameMethod.class).to(RenameMethodImplementer.class);
		bind(IRefactoringImplementer.class).annotatedWith(RenameType.class).to(RenameTypeImplementer.class);
		bind(IRefactoringImplementer.class).annotatedWith(MoveResource.class).to(MoveRefactoringImplementer.class);
	}

}
