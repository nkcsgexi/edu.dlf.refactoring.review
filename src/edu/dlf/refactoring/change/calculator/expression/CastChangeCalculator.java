package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.CastExpression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeComponentInjector.CastAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class CastChangeCalculator implements IASTNodeChangeCalculator{

	private final ChangeBuilder builder;
	private final Logger logger;
	private final IASTNodeChangeCalculator typeCal;
	private final IASTNodeChangeCalculator expCal;

	@Inject
	public CastChangeCalculator(Logger logger, 
			@CastAnnotation String castLV,
			@ExpressionAnnotation IASTNodeChangeCalculator expCal,
			@TypeAnnotation IASTNodeChangeCalculator typeCal){
		this.logger = logger;
		this.builder = new ChangeBuilder(castLV);
		this.expCal = expCal;
		this.typeCal = typeCal;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange simpleChange = builder.buildSimpleChange(pair);
		if(simpleChange != null)
			return simpleChange;
		SubChangeContainer container = builder.createSubchangeContainer(pair);
		ASTNodePair expPair = pair.selectByPropertyDescriptor(CastExpression.
			EXPRESSION_PROPERTY);
		ASTNodePair typePair = pair.selectByPropertyDescriptor(CastExpression.
			TYPE_PROPERTY);
		container.addSubChange(typeCal.CalculateASTNodeChange(typePair));
		container.addSubChange(expCal.CalculateASTNodeChange(expPair));
		return container;
	}

}
