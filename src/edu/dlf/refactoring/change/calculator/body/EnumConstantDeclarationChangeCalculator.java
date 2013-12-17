package edu.dlf.refactoring.change.calculator.body;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnumConstantDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class EnumConstantDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder enumConstantCB;
	private final IASTNodeChangeCalculator simpleNameCal;

	@Inject
	public EnumConstantDeclarationChangeCalculator(Logger logger,
			@EnumConstantDeclarationAnnotation String changeLV,
			@SimpleNameAnnotation IASTNodeChangeCalculator simpleNameCal) {
		this.logger = logger;
		this.enumConstantCB = new ChangeBuilder(changeLV);
		this.simpleNameCal = simpleNameCal;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = enumConstantCB.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = enumConstantCB.createSubchangeContainer
			(pair);
		ASTNodePair namePair = pair.selectByPropertyDescriptor
			(EnumConstantDeclaration.NAME_PROPERTY);
		container.addSubChange(simpleNameCal.CalculateASTNodeChange(namePair));
		return container;
	}

}
