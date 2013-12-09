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
import edu.dlf.refactoring.change.calculator.FieldDeclarationCalculator;
import edu.dlf.refactoring.change.calculator.MethodChangeCalculator;
import edu.dlf.refactoring.change.calculator.ProjectChangeCalculator;
import edu.dlf.refactoring.change.calculator.SourcePackageChangeCalculator;
import edu.dlf.refactoring.change.calculator.TypeDeclarationChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.AssignmentChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.CastChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.ClassInstanceCreateCalculator;
import edu.dlf.refactoring.change.calculator.expression.ExpressionChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.FieldAccessChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.InfixExpressionChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.InstanceOfChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.MethodInvocationChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.NameChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.PrePostFixExpressionChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.QualifiedNameChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.SimpleNameChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.SingleVariableDeclarationChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.ThisChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.TypeChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.VariableDeclarationChangeCalculator;
import edu.dlf.refactoring.change.calculator.expression.VariableDeclarationFragmentChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.BlockChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.CatchClauseChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.DoStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.ForStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.IfStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.KeyWordsStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.ReturnAndThrowStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.StatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.TryStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.VariableDeclarationStatementChangeCalculator;
import edu.dlf.refactoring.change.calculator.statement.WhileStatementChangeCalculator;

public class ChangeComponentInjector extends AbstractModule{
	
		//
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface TypeAnnotation {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface FieldDeclarationAnnotation {}

		

		// Expressions
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ExpressionAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface InstanceOfExpressionAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ClassInstanceCreationAnnotation {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface InfixExpressionAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface InfixExpressionOperatorAnnotation{}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface FieldAccessAnnotation{}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, 	METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface VariableDeclarationAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface SingleVariableDeclarationAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface VariableDeclarationFragmentAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface AssignmentAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface CastAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ThisAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface NameAnnotation {}
	
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface QualifiedNameAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface SimpleNameAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface PrePostFixExpressionAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface LiteralAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface MethodInvocationAnnotation {}		

		
		
		// Statements
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface StatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface IfStatementAnnotation {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ForStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface BlockAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface DoStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface WhileStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface TryStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface CatchClauseAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface BreakStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ContinueStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ReturnStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface ThrowStatementAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface VariableDeclarationStatementAnnotation {}
		
		// Others
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface TypeDeclarationAnnotation {}
		
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface MethodDeclarationAnnotation {}
		
		
		// JavaModels:
		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface JavaProjectAnnotation {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface SourcePackageAnnotation {}

		@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
		public @interface CompilationUnitAnnotation {}
		
		
		@Override
		protected void configure() {
			
			bindJavaModelCalculators();
			bindASTCalculators();
			bindAnnotationToStrings();	
		}
		
		
		private void bindJavaModelCalculators()
		{
			bind(IJavaModelChangeCalculator.class).annotatedWith(CompilationUnitAnnotation.class).
				to(CompilationUnitChangeCalculator.class);
			bind(IJavaModelChangeCalculator.class).annotatedWith(SourcePackageAnnotation.class).
				to(SourcePackageChangeCalculator.class);
			bind(IJavaModelChangeCalculator.class).annotatedWith(JavaProjectAnnotation.class).
				to(ProjectChangeCalculator.class);
		}
		
		
		private void bindASTCalculators()
		{
			bind(IASTNodeChangeCalculator.class).annotatedWith(CompilationUnitAnnotation.class).to(CompilationUnitChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(MethodDeclarationAnnotation.class).to(MethodChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(TypeDeclarationAnnotation.class).to(TypeDeclarationChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(FieldDeclarationAnnotation.class).to(FieldDeclarationCalculator.class);
			
			bind(IASTNodeChangeCalculator.class).annotatedWith(IfStatementAnnotation.class).to(IfStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(StatementAnnotation.class).to(StatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(BlockAnnotation.class).to(BlockChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ForStatementAnnotation.class).to(ForStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(WhileStatementAnnotation.class).to(WhileStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(DoStatementAnnotation.class).to(DoStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(TryStatementAnnotation.class).to(TryStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(CatchClauseAnnotation.class).to(CatchClauseChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(BreakStatementAnnotation.class).to(KeyWordsStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ContinueStatementAnnotation.class).to(KeyWordsStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ReturnStatementAnnotation.class).to(ReturnAndThrowStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ThrowStatementAnnotation.class).to(ReturnAndThrowStatementChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(VariableDeclarationStatementAnnotation.class).to(VariableDeclarationStatementChangeCalculator.class);
			
			
			bind(IASTNodeChangeCalculator.class).annotatedWith(ExpressionAnnotation.class).to(ExpressionChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(InstanceOfExpressionAnnotation.class).to(InstanceOfChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ClassInstanceCreationAnnotation.class).to(ClassInstanceCreateCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(AssignmentAnnotation.class).to(AssignmentChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(VariableDeclarationAnnotation.class).to(VariableDeclarationChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(SingleVariableDeclarationAnnotation.class).to(SingleVariableDeclarationChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(VariableDeclarationFragmentAnnotation.class).to(VariableDeclarationFragmentChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(TypeAnnotation.class).to(TypeChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(SimpleNameAnnotation.class).to(SimpleNameChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(NameAnnotation.class).to(NameChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(QualifiedNameAnnotation.class).to(QualifiedNameChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(PrePostFixExpressionAnnotation.class).to(PrePostFixExpressionChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(InfixExpressionAnnotation.class).to(InfixExpressionChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(MethodInvocationAnnotation.class).to(MethodInvocationChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(FieldAccessAnnotation.class).to(FieldAccessChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(ThisAnnotation.class).to(ThisChangeCalculator.class);
			bind(IASTNodeChangeCalculator.class).annotatedWith(CastAnnotation.class).to(CastChangeCalculator.class);
			
		}


		private void bindAnnotationToStrings() {
			bindConstant().annotatedWith(NameAnnotation.class).to("Name");
			bindConstant().annotatedWith(QualifiedNameAnnotation.class).to("QualifiedName");
			bindConstant().annotatedWith(PrePostFixExpressionAnnotation.class).to("PrePostFix");
			bindConstant().annotatedWith(InfixExpressionAnnotation.class).to("InfexExpression");
			bindConstant().annotatedWith(InfixExpressionOperatorAnnotation.class).to("InfexExpressionOperator");
			bindConstant().annotatedWith(SimpleNameAnnotation.class).to("SimpleName");
			bindConstant().annotatedWith(TypeAnnotation.class).to("Type");
			bindConstant().annotatedWith(ExpressionAnnotation.class).to("Expression");
			bindConstant().annotatedWith(InstanceOfExpressionAnnotation.class).to("InstanceOfExpression");
			bindConstant().annotatedWith(ClassInstanceCreationAnnotation.class).to("ClassInstanceCreation");
			bindConstant().annotatedWith(VariableDeclarationAnnotation.class).to("VariableDeclaration");
			bindConstant().annotatedWith(SingleVariableDeclarationAnnotation.class).to("SingleVariableDeclaration");
			bindConstant().annotatedWith(VariableDeclarationFragmentAnnotation.class).to("VariableDeclarationFragment");
			bindConstant().annotatedWith(AssignmentAnnotation.class).to("Assignment");
			bindConstant().annotatedWith(MethodInvocationAnnotation.class).to("MethodInvocation");
			bindConstant().annotatedWith(FieldAccessAnnotation.class).to("FieldAccess");
			bindConstant().annotatedWith(CastAnnotation.class).to("CastExpression");
			bindConstant().annotatedWith(ThisAnnotation.class).to("ThisExpression");
			
			bindConstant().annotatedWith(StatementAnnotation.class).to("Statement");
			bindConstant().annotatedWith(ForStatementAnnotation.class).to("ForStatement");
			bindConstant().annotatedWith(IfStatementAnnotation.class).to("IfStatement");
			bindConstant().annotatedWith(WhileStatementAnnotation.class).to("WhileStatement");
			bindConstant().annotatedWith(DoStatementAnnotation.class).to("DoStatement");
			bindConstant().annotatedWith(TryStatementAnnotation.class).to("TryStatement");
			bindConstant().annotatedWith(CatchClauseAnnotation.class).to("CatchClause");
			bindConstant().annotatedWith(BlockAnnotation.class).to("BlockStatement");
			bindConstant().annotatedWith(ContinueStatementAnnotation.class).to("ContinueStatement");
			bindConstant().annotatedWith(BreakStatementAnnotation.class).to("BreakStatement");
			bindConstant().annotatedWith(ReturnStatementAnnotation.class).to("ReturnStatement");
			bindConstant().annotatedWith(ThrowStatementAnnotation.class).to("ThrowStatement");
			bindConstant().annotatedWith(VariableDeclarationStatementAnnotation.class).to("VariableDeclarationStatement");
			
			bindConstant().annotatedWith(FieldDeclarationAnnotation.class).to("FieldDeclaration");
			bindConstant().annotatedWith(TypeDeclarationAnnotation.class).to("TypeDeclaration");
			bindConstant().annotatedWith(MethodDeclarationAnnotation.class).to("Method");
			bindConstant().annotatedWith(CompilationUnitAnnotation.class).to("CompilationUnit");
			bindConstant().annotatedWith(SourcePackageAnnotation.class).to("SourcePackage");
			bindConstant().annotatedWith(JavaProjectAnnotation.class).to("JavaProject");
		}

}

