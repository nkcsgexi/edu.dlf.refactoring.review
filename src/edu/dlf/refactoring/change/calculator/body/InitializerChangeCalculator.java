package edu.dlf.refactoring.change.calculator.body;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.Initializer;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InitializerAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class InitializerChangeCalculator implements IASTNodeChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator blockCal;

	@Inject
	public InitializerChangeCalculator(Logger logger,
			@InitializerAnnotation String changeLV,
			@BlockAnnotation IASTNodeChangeCalculator blockCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.blockCal = blockCal;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(this.blockCal.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(Initializer.BODY_PROPERTY)));
		return container;
	}

}
