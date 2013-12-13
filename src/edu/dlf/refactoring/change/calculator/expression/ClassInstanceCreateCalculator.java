package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AnonymousClassDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ClassInstanceCreationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.data.List;

public class ClassInstanceCreateCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final IASTNodeChangeCalculator expressionCal;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator typeCal;
	private final IASTNodeChangeCalculator anonymousClassCal;

	@Inject
	public ClassInstanceCreateCalculator(
		Logger logger,
		@TypeAnnotation IASTNodeChangeCalculator typeCal,
		@ExpressionAnnotation IASTNodeChangeCalculator expressionCal,
		@AnonymousClassDeclarationAnnotation IASTNodeChangeCalculator anonymousClassCal,
		@ClassInstanceCreationAnnotation String changeLV) {
		this.logger = logger;
		this.expressionCal = expressionCal;
		this.typeCal = typeCal;
		this.anonymousClassCal = anonymousClassCal;
		this.changeBuilder = new ChangeBuilder(changeLV);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer
			(pair);
		container.addSubChange(typeCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(ClassInstanceCreation.TYPE_PROPERTY)));
		List<ISourceChange> expressionChangess = pair.selectNodePairByChildrenDescriptor
			(ClassInstanceCreation.ARGUMENTS_PROPERTY).map(ASTNodePair.splitPairFunc.
				andThen(SourceChangeUtils.getChangeCalculationFunc(expressionCal).
					tuple()));
		container.addMultiSubChanges(expressionChangess.toCollection());
		container.addSubChange(anonymousClassCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(ClassInstanceCreation.
				ANONYMOUS_CLASS_DECLARATION_PROPERTY)));
		return container;
	}
}
