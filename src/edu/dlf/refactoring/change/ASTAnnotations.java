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

import edu.dlf.refactoring.change.calculator.MethodChangeCalculator;
import edu.dlf.refactoring.change.calculator.TypeDeclarationChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.ExpressionChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.BlockChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.IfStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.StatementChangeCalculator;

public class ASTAnnotations extends AbstractModule{
	
		//
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface SimpleNameAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface TypeAnnotation {}


		// Expressions
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ExpressionAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface VariableDeclarationAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface VariableDeclarationFragmentAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface AssignmentAnnotation {}
	
		
		// Statements
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface StatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface IfStatementAnnotation {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface BlockAnnotation {}
		
		
		// Others
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface TypeDeclarationAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface MethodDeclarationAnnotation {}

		
		@Override
		protected void configure() {
			bind(IASTNodeChangeCalculator.class).annotatedWith(MethodDeclarationAnnotation.class).to(MethodChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(TypeDeclarationAnnotation.class).to(TypeDeclarationChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(IfStatementAnnotation.class).to(IfStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(StatementAnnotation.class).to(StatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(BlockAnnotation.class).to(BlockChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ExpressionAnnotation.class).to(ExpressionChangeCalculator.class);
			
			bindConstant().annotatedWith(TypeAnnotation.class).to("Type");
			bindConstant().annotatedWith(ExpressionAnnotation.class).to("Expression");
			bindConstant().annotatedWith(VariableDeclarationAnnotation.class).to("VariableDeclaration");
			bindConstant().annotatedWith(VariableDeclarationFragmentAnnotation.class).to("VariableDeclarationFragment");
			bindConstant().annotatedWith(AssignmentAnnotation.class).to("Assignment");
			bindConstant().annotatedWith(StatementAnnotation.class).to("Statement");
			bindConstant().annotatedWith(IfStatementAnnotation.class).to("IfStatement");
			bindConstant().annotatedWith(BlockAnnotation.class).to("Block");
			bindConstant().annotatedWith(TypeDeclarationAnnotation.class).to("TypeDeclaration");
			bindConstant().annotatedWith(MethodDeclarationAnnotation.class).to("Method");
		}

}

