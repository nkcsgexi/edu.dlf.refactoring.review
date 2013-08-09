package edu.dlf.refactoring.change;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;

import edu.dlf.refactoring.change.calculator.CompilationUnitChangeCalculator;
import edu.dlf.refactoring.change.calculator.ProjectChangeCalculator;
import edu.dlf.refactoring.change.calculator.SourcePackageChangeCalculator;

public class JavaModelAnnotation extends AbstractModule{

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface JavaProject {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface SourcePackage {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface CompilationUnit {}

		@Override
		protected void configure() {
			bind(IJavaModelChangeCalculator.class).annotatedWith(CompilationUnit.class).to(CompilationUnitChangeCalculator.class);
			bind(IJavaModelChangeCalculator.class).annotatedWith(SourcePackage.class).to(SourcePackageChangeCalculator.class);
			bind(IJavaModelChangeCalculator.class).annotatedWith(JavaProject.class).to(ProjectChangeCalculator.class);
		}
}
