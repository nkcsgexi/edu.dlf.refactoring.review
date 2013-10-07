package edu.dlf.refactoring.detectors;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;

import edu.dlf.refactoring.checkers.ExtractMethodChecker;
import edu.dlf.refactoring.checkers.RenameMethodChecker;
import edu.dlf.refactoring.checkers.RenameTypeChecker;
import edu.dlf.refactoring.design.IRefactoringChecker;
import edu.dlf.refactoring.design.IRefactoringDetector;
import edu.dlf.refactoring.design.RefactoringProcessor;
import edu.dlf.refactoring.design.RefactoringType;

public class RefactoringDetectionComponentInjector extends AbstractModule{

	@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
	public @interface ExtractMethod {}

	@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
	public @interface RenameMethod {}

	@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
	public @interface RenameType {}

	@Override
	protected void configure() {
		bind(IRefactoringDetector.class).annotatedWith(RenameMethod.class).to(RenameMethodDetector.class);
		bind(IRefactoringDetector.class).annotatedWith(ExtractMethod.class).to(ExtractMethodDetector.class);
		bind(IRefactoringDetector.class).annotatedWith(RenameType.class).to(RenameTypeDetector.class);
		
		bind(IRefactoringChecker.class).annotatedWith(RenameMethod.class).to(RenameMethodChecker.class);
		bind(IRefactoringChecker.class).annotatedWith(RenameType.class).to(RenameTypeChecker.class);
		bind(IRefactoringChecker.class).annotatedWith(ExtractMethod.class).to(ExtractMethodChecker.class);
			
		bindConstant().annotatedWith(ExtractMethod.class).to(RefactoringType.ExtractMethod);
		bindConstant().annotatedWith(RenameMethod.class).to(RefactoringType.RenameMethod);
		bindConstant().annotatedWith(RenameType.class).to(RefactoringType.RenameType);
	}
		
}