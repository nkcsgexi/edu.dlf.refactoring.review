package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ThisExpression;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ThisAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class ThisChangeCalculator implements IASTNodeChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder builder;
	private final IASTNodeChangeCalculator nameCal;

	@Inject
	public ThisChangeCalculator(Logger logger, 
			@ThisAnnotation String thisLV,
			@NameAnnotation IASTNodeChangeCalculator nameCal) {
		this.logger = logger;
		this.builder = new ChangeBuilder(thisLV);
		this.nameCal = nameCal;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange simpleChange = this.builder.buildSimpleChange(pair);
		if(simpleChange != null)
			return simpleChange;
		SubChangeContainer container = this.builder.createSubchangeContainer(pair);
		ASTNodePair quPair = pair.selectByPropertyDescriptor(ThisExpression.
			QUALIFIER_PROPERTY);
		container.addSubChange(nameCal.CalculateASTNodeChange(quPair));
		return container;
	}

}
