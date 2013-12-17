package edu.dlf.refactoring.change.calculator.body;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AnnotationTypeDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.AnnotationTypeMemberDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.BodyDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnumConstantDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnumDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InitializerAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class BodyDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator typeDeclarationCal;
	private final IASTNodeChangeCalculator enumDeclarationCal;
	private final IASTNodeChangeCalculator fieldDeclarationCal;
	private final IASTNodeChangeCalculator methodDeclarationCal;
	private final IASTNodeChangeCalculator initializerCal;
	private final IASTNodeChangeCalculator enumConstantCal;
	private final IASTNodeChangeCalculator annotationTypeMemberDeclarationCal;
	private final IASTNodeChangeCalculator annotationTypeDeclarationCal;

	@Inject
	public BodyDeclarationChangeCalculator(Logger logger,
			@BodyDeclarationAnnotation String bodyDeclarationLV,
			@TypeDeclarationAnnotation IASTNodeChangeCalculator typeDeclarationCal,
			@EnumDeclarationAnnotation IASTNodeChangeCalculator enumDeclarationCal,
			@EnumConstantDeclarationAnnotation IASTNodeChangeCalculator enumConstantCal,
			@MethodDeclarationAnnotation IASTNodeChangeCalculator methodDeclarationCal,
			@FieldDeclarationAnnotation IASTNodeChangeCalculator fieldDeclarationCal,
			@InitializerAnnotation IASTNodeChangeCalculator initializerCal,
			@AnnotationTypeDeclarationAnnotation IASTNodeChangeCalculator annotationTypeDeclarationCal,
			@AnnotationTypeMemberDeclarationAnnotation IASTNodeChangeCalculator 
				annotationTypeMemberDeclarationCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(bodyDeclarationLV);
		this.typeDeclarationCal = typeDeclarationCal;
		this.enumDeclarationCal = enumDeclarationCal;
		this.enumConstantCal = enumConstantCal;
		this.methodDeclarationCal = methodDeclarationCal;
		this.fieldDeclarationCal = fieldDeclarationCal;
		this.initializerCal = initializerCal;
		this.annotationTypeDeclarationCal = annotationTypeDeclarationCal;
		this.annotationTypeMemberDeclarationCal = annotationTypeMemberDeclarationCal;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		if(pair.getNodeAfter().getNodeType() != pair.getNodeBefore().getNodeType()) 
			return changeBuilder.createUnknownChange(pair);
		switch(pair.getNodeBefore().getNodeType()) {
		case ASTNode.TYPE_DECLARATION:
			return this.typeDeclarationCal.CalculateASTNodeChange(pair);
		case ASTNode.ENUM_DECLARATION:
			return this.enumDeclarationCal.CalculateASTNodeChange(pair);
		case ASTNode.ENUM_CONSTANT_DECLARATION:
			return this.enumConstantCal.CalculateASTNodeChange(pair);
		case ASTNode.METHOD_DECLARATION:
			return this.methodDeclarationCal.CalculateASTNodeChange(pair);
		case ASTNode.FIELD_DECLARATION:
			return this.fieldDeclarationCal.CalculateASTNodeChange(pair);
		case ASTNode.INITIALIZER:
			return this.initializerCal.CalculateASTNodeChange(pair);
		case ASTNode.ANNOTATION_TYPE_DECLARATION:
			return this.annotationTypeDeclarationCal.CalculateASTNodeChange(pair);
		case ASTNode.ANNOTATION_TYPE_MEMBER_DECLARATION:
			return this.annotationTypeMemberDeclarationCal.CalculateASTNodeChange(pair);
		default: 
			return changeBuilder.createUnknownChange(pair);
		}
	}

}
