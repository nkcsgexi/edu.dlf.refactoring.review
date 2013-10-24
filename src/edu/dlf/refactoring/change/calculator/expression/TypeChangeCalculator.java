package edu.dlf.refactoring.change.calculator.expression;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F3;

public class TypeChangeCalculator implements IASTNodeChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator nameCalculator;

	@Inject
	public TypeChangeCalculator(Logger logger, 
		@NameAnnotation IASTNodeChangeCalculator nameCalculator,	
		@TypeAnnotation String changeLevel)
	{
		this.logger = logger;
		this.nameCalculator = nameCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		F3<ASTNode, ASTNode, Integer, Boolean> areTypesSame = 
			new F3<ASTNode, ASTNode, Integer, Boolean>(){
			@Override
			public Boolean f(ASTNode n1, ASTNode n2, Integer kind) {
				return n1.getNodeType() == kind && n2.getNodeType() == kind;
			}};
		if(areTypesSame.f(pair.getNodeBefore(), pair.getNodeAfter(), ASTNode.SIMPLE_TYPE))
		{
			ASTNodePair names = pair.selectByPropertyDescriptor(SimpleType.NAME_PROPERTY);
			container.addSubChange(this.nameCalculator.CalculateASTNodeChange(names));
		}
		if(areTypesSame.f(pair.getNodeBefore(), pair.getNodeAfter(), ASTNode.QUALIFIED_TYPE))
		{
			ASTNodePair names = pair.selectByPropertyDescriptor(QualifiedType.NAME_PROPERTY);
			ASTNodePair types = pair.selectByPropertyDescriptor(QualifiedType.QUALIFIER_PROPERTY);
			container.addSubChange(CalculateASTNodeChange(types));
			container.addSubChange(this.nameCalculator.CalculateASTNodeChange(names));
		}
		
		if(areTypesSame.f(pair.getNodeBefore(), pair.getNodeAfter(), ASTNode.PRIMITIVE_TYPE))
		{
			return changeBuilder.createUpdateNodesChange(pair);
		}
		
		if(areTypesSame.f(pair.getNodeBefore(), pair.getNodeAfter(), ASTNode.ARRAY_TYPE))
		{
			ASTNodePair types = pair.selectByPropertyDescriptor(ArrayType.COMPONENT_TYPE_PROPERTY);
			container.addSubChange(CalculateASTNodeChange(types));
		}
		// missing parameterized types.
		return container;
	}


}
