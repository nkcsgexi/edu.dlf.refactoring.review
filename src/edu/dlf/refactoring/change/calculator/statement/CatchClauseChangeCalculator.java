package edu.dlf.refactoring.change.calculator.statement;

import org.eclipse.jdt.core.dom.CatchClause;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.CatchClauseAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class CatchClauseChangeCalculator implements IASTNodeChangeCalculator {
	
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator vdCalculator;
	private final IASTNodeChangeCalculator blCalculator;

	@Inject
	public CatchClauseChangeCalculator(
			@CatchClauseAnnotation String changeLevel,
			@VariableDeclarationAnnotation IASTNodeChangeCalculator vdCalculator,
			@BlockAnnotation IASTNodeChangeCalculator blCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.vdCalculator = vdCalculator;
		this.blCalculator = blCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer();
		container.addSubChange(vdCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor
				(CatchClause.EXCEPTION_PROPERTY)));
		container.addSubChange(blCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor
				(CatchClause.BODY_PROPERTY)));
		return container;
	}

}
