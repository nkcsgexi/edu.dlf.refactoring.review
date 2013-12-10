package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SingleVariableDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class SingleVariableDeclarationChangeCalculator implements IASTNodeChangeCalculator {
	
	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snCalculator;
	private final IASTNodeChangeCalculator tCalculator;

	@Inject
	public SingleVariableDeclarationChangeCalculator(Logger logger,
			@SingleVariableDeclarationAnnotation String level,
			@TypeAnnotation IASTNodeChangeCalculator tCalculator,
			@SimpleNameAnnotation IASTNodeChangeCalculator snCalculator) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(level);
		this.tCalculator = tCalculator;
		this.snCalculator = snCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer
			(pair);
		ASTNodePair typePair = pair.selectByPropertyDescriptor
			(SingleVariableDeclaration.TYPE_PROPERTY);
		ASTNodePair namePair = pair.selectByPropertyDescriptor
			(SingleVariableDeclaration.NAME_PROPERTY);
		container.addSubChange(this.tCalculator.CalculateASTNodeChange(typePair));
		container.addSubChange(this.snCalculator.CalculateASTNodeChange(namePair));
		return container;
	}

}
