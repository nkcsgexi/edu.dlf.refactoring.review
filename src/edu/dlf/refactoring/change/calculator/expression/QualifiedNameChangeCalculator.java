package edu.dlf.refactoring.change.calculator.expression;

import org.eclipse.jdt.core.dom.QualifiedName;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.QualifiedNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;

public class QualifiedNameChangeCalculator implements IASTNodeChangeCalculator {

	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator nCalculator;
	private final IASTNodeChangeCalculator snCalculator;

	@Inject
	public QualifiedNameChangeCalculator(
			@QualifiedNameAnnotation String changeLevel,
			@NameAnnotation IASTNodeChangeCalculator nCalculator,
			@SimpleNameAnnotation IASTNodeChangeCalculator snCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.nCalculator = nCalculator;
		this.snCalculator = snCalculator;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(this.nCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor
				(QualifiedName.QUALIFIER_PROPERTY)));
		container.addSubChange(this.snCalculator.CalculateASTNodeChange(pair.selectByPropertyDescriptor
				(QualifiedName.NAME_PROPERTY)));
		return container;
	}

}
