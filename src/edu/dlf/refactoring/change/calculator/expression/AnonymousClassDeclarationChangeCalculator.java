package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.AnonymousClassDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.BodyDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class AnonymousClassDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final IASTNodeChangeCalculator bodyDeclarationCal;
	private final ChangeBuilder changeBuilder;
	
	@Inject
	public AnonymousClassDeclarationChangeCalculator(Logger logger,
			@AnonymousClassDeclarationAnnotation String changeLV, 
			@BodyDeclarationAnnotation IASTNodeChangeCalculator bodyDeclarationCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.bodyDeclarationCal = bodyDeclarationCal;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		ASTNodePair bodyPair = pair.selectByPropertyDescriptor
			(AnonymousClassDeclaration.BODY_DECLARATIONS_PROPERTY);
		container.addSubChange(this.bodyDeclarationCal.CalculateASTNodeChange
			(bodyPair));
		return container;
	}
}
