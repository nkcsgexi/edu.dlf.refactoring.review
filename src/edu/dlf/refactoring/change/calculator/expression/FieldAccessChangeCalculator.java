package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.FieldAccess;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldAccessAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class FieldAccessChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder builder;
	private final IASTNodeChangeCalculator expCal;
	private final IASTNodeChangeCalculator nameCal;

	@Inject
	public FieldAccessChangeCalculator(
		Logger logger,
		@FieldAccessAnnotation String fieldAccessLV,
		@NameAnnotation IASTNodeChangeCalculator nameCal,
		@ExpressionAnnotation IASTNodeChangeCalculator expCal) {
		this.logger = logger;
		this.nameCal = nameCal;
		this.expCal = expCal;
		this.builder = new ChangeBuilder(fieldAccessLV);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange simpleChange = builder.buildSimpleChange(pair);
		if(simpleChange != null)
			return simpleChange;
		SubChangeContainer container = this.builder.createSubchangeContainer(pair);
		ASTNodePair namePair = pair.selectByPropertyDescriptor(FieldAccess.NAME_PROPERTY);
		ASTNodePair expPair = pair.selectByPropertyDescriptor(FieldAccess.EXPRESSION_PROPERTY);
		container.addSubChange(nameCal.CalculateASTNodeChange(namePair));
		container.addSubChange(expCal.CalculateASTNodeChange(expPair));
		return container;
	}

}
