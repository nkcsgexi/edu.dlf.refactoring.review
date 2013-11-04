package edu.dlf.refactoring.hiding;

import com.google.inject.AbstractModule;

import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;

public class HidingComponentInjector extends AbstractModule{

	
	
	
	@Override
	protected void configure() {
		bind(AbstractRefactoringHider.class).annotatedWith(ExtractMethod.class).to(ExtractMethodHider.class);

	}

}
