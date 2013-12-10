package edu.dlf.refactoring.change.calculator.expression;

import static fj.data.List.list;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.change.AbstractGeneralChangeCalculator;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AssignmentAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.CastAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ClassInstanceCreationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldAccessAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InfixExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.InstanceOfExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.LiteralAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodInvocationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.NameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.PrePostFixExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ThisAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.UpdateASTNodeChange;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.Equal;
import fj.F2;
import fj.P2;
import fj.data.List;

public class ExpressionChangeCalculator extends AbstractGeneralChangeCalculator{

	private final IASTNodeChangeCalculator vdCalculator;
	private final IASTNodeChangeCalculator asCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator nCalculator;
	private final IASTNodeChangeCalculator ppfCalculator;
	private final IASTNodeChangeCalculator infCalculator;
	private final IASTNodeChangeCalculator miCalculator;
	private final IASTNodeChangeCalculator fieldAccessCal;
	private final IASTNodeChangeCalculator castExpCal;
	private final IASTNodeChangeCalculator thisExpCal;
	private final Logger logger;
	private final IASTNodeChangeCalculator creatorCal;
	private final IASTNodeChangeCalculator instanceOfCal;
	private final String literalLV;

	@Inject
	public ExpressionChangeCalculator(
			Logger logger,
			@ExpressionAnnotation String changeLevel,
			@LiteralAnnotation String literalLV,
			@InstanceOfExpressionAnnotation IASTNodeChangeCalculator instanceOfCal,
			@VariableDeclarationAnnotation IASTNodeChangeCalculator vdCalculator,			
			@AssignmentAnnotation IASTNodeChangeCalculator asCalculator,
			@NameAnnotation IASTNodeChangeCalculator nCalculator,
			@ClassInstanceCreationAnnotation IASTNodeChangeCalculator creatorCal,
			@PrePostFixExpressionAnnotation IASTNodeChangeCalculator ppfCalculator,
			@InfixExpressionAnnotation IASTNodeChangeCalculator infCalculator,
			@MethodInvocationAnnotation IASTNodeChangeCalculator miCalculator,
			@FieldAccessAnnotation IASTNodeChangeCalculator fieldAccessCal,
			@ThisAnnotation IASTNodeChangeCalculator thisExpCal,
			@CastAnnotation IASTNodeChangeCalculator castExpCal) {
		this.logger = logger;
		this.asCalculator = asCalculator;
		this.vdCalculator = vdCalculator;
		this.nCalculator = nCalculator;
		this.ppfCalculator = ppfCalculator;
		this.infCalculator = infCalculator;
		this.miCalculator = miCalculator;
		this.fieldAccessCal = fieldAccessCal;
		this.thisExpCal = thisExpCal;
		this.castExpCal = castExpCal;
		this.creatorCal = creatorCal;
		this.instanceOfCal = instanceOfCal;
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.literalLV = literalLV;
	}
	
	private final F2<ASTNode, ASTNode, ISourceChange> expChangeCalculationFunc = 
			SourceChangeUtils.getChangeCalculationFunc(this);
	
	private final List<Integer> literalTypes = List.list(ASTNode.STRING_LITERAL,
		ASTNode.NULL_LITERAL, ASTNode.NUMBER_LITERAL, ASTNode.CHARACTER_LITERAL,
		ASTNode.BOOLEAN_LITERAL);
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		
		Expression expBefore, expAfter;
		expBefore = (Expression) pair.getNodeBefore();
		expAfter = (Expression) pair.getNodeAfter();
		
		if(literalTypes.find(Equal.intEqual.eq().f(expBefore.getNodeType())).isSome()
		 && literalTypes.find(Equal.intEqual.eq().f(expAfter.getNodeType())).isSome())
		{
			return new UpdateASTNodeChange(pair, literalLV);
		}
		
		
		if(expBefore.getNodeType() != expAfter.getNodeType()) {
			logger.debug("Before expression: " + pair.getNodeBefore());
			logger.debug("After expression: " + pair.getNodeAfter());
			List<List<ASTNode>> groupsBefore = getDecendantExpressions.f(expBefore).
				snoc(expBefore).group(typeEq);
			List<List<ASTNode>> groupsAfter = getDecendantExpressions.f(expAfter).
				snoc(expAfter).group(typeEq);
			List<P2<List<ASTNode>, List<ASTNode>>> groupPairs = FJUtils.
				getSamePairs(groupsBefore, groupsAfter, listTypeEq);
			List<P2<ASTNode, ASTNode>> nodePairs = removeSubPairs(groupPairs.bind
				(similarNodeMapper.tuple()).filter(areBothNotNull)).
					sort(orderByFirstNodeStart);
			SubChangeContainer container = changeBuilder.
				createSubchangeContainer(pair);
			container.addMultiSubChanges(nodePairs.map(expChangeCalculationFunc.
				tuple()).toCollection());
			container = pruneSourceChangeContainer(container);
			return container == null ? changeBuilder.createUnknownChange(pair) 
				: container;
		}
		
		if(expBefore.getNodeType() == ASTNode.PARENTHESIZED_EXPRESSION) {
			ASTNodePair internalPair = pair.selectByPropertyDescriptor
				(ParenthesizedExpression.EXPRESSION_PROPERTY);
			return this.CalculateASTNodeChange(internalPair);
		}
		
		if(expBefore.getNodeType() == ASTNode.CONDITIONAL_EXPRESSION) {
			SubChangeContainer container = changeBuilder.createSubchangeContainer
				(pair);
			List<ASTNodePair> internalPairs = list(
				pair.selectByPropertyDescriptor
					(ConditionalExpression.EXPRESSION_PROPERTY),
				pair.selectByPropertyDescriptor
					(ConditionalExpression.THEN_EXPRESSION_PROPERTY),
				pair.selectByPropertyDescriptor
					(ConditionalExpression.ELSE_EXPRESSION_PROPERTY));
			container.addMultiSubChanges(internalPairs.map(ASTNodePair.
				splitPairFunc.andThen(expChangeCalculationFunc.tuple())).
					toCollection());
			return container;
		}
	
		switch(expBefore.getNodeType()) {
		case ASTNode.CLASS_INSTANCE_CREATION:
			return this.creatorCal.CalculateASTNodeChange(pair);
		case ASTNode.ASSIGNMENT:
			return this.asCalculator.CalculateASTNodeChange(pair);
		case ASTNode.VARIABLE_DECLARATION_EXPRESSION:
			return this.vdCalculator.CalculateASTNodeChange(pair);
		case ASTNode.INSTANCEOF_EXPRESSION:
			return this.instanceOfCal.CalculateASTNodeChange(pair);
		case ASTNode.PREFIX_EXPRESSION:
			return this.ppfCalculator.CalculateASTNodeChange(pair);
		case ASTNode.POSTFIX_EXPRESSION:
			return this.ppfCalculator.CalculateASTNodeChange(pair);
		case ASTNode.SIMPLE_NAME:
			return this.nCalculator.CalculateASTNodeChange(pair);
		case ASTNode.QUALIFIED_NAME:
			return this.nCalculator.CalculateASTNodeChange(pair);
		case ASTNode.INFIX_EXPRESSION:
			return this.infCalculator.CalculateASTNodeChange(pair);	
		case ASTNode.METHOD_INVOCATION:
			return this.miCalculator.CalculateASTNodeChange(pair);
		case ASTNode.FIELD_ACCESS: 
			return this.fieldAccessCal.CalculateASTNodeChange(pair);
		case ASTNode.THIS_EXPRESSION: 	
			return this.thisExpCal.CalculateASTNodeChange(pair);
		case ASTNode.CAST_EXPRESSION: 	
			return this.castExpCal.CalculateASTNodeChange(pair);
		default: 
			return changeBuilder.createUnknownChange(pair);
		}
	}
}
