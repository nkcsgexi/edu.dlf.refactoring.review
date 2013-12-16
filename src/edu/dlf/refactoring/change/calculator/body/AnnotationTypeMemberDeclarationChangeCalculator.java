package edu.dlf.refactoring.change.calculator.body;
import static fj.data.List.list;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AnnotationTypeMemberDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.ExpressionAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class AnnotationTypeMemberDeclarationChangeCalculator implements
		IASTNodeChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final List<F<List<ASTNodePair>, List<ISourceChange>>> changeMappers;
	private final List<StructuralPropertyDescriptor> descriptors;

	@Inject
	public AnnotationTypeMemberDeclarationChangeCalculator(
			Logger logger,
			@AnnotationTypeMemberDeclarationAnnotation String changeLV,
			@TypeAnnotation IASTNodeChangeCalculator typeCal,
			@SimpleNameAnnotation IASTNodeChangeCalculator simpleNameCal,
			@ExpressionAnnotation IASTNodeChangeCalculator expressionCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		F<ASTNodePair, ISourceChange> typeCalFunc = ASTNodePair.splitPairFunc.
			andThen(SourceChangeUtils.getChangeCalculationFunc(typeCal).tuple());
		F<ASTNodePair, ISourceChange> simpleNameCalFunc = ASTNodePair.splitPairFunc.
			andThen(SourceChangeUtils.getChangeCalculationFunc(simpleNameCal).tuple());
		F<ASTNodePair, ISourceChange> expressionCalFunc = ASTNodePair.splitPairFunc.
			andThen(SourceChangeUtils.getChangeCalculationFunc(expressionCal).tuple());
		this.changeMappers = list(typeCalFunc.mapList(), 
			simpleNameCalFunc.mapList(), expressionCalFunc.mapList());
		this.descriptors = list(
			(StructuralPropertyDescriptor)AnnotationTypeMemberDeclaration.TYPE_PROPERTY, 
			(StructuralPropertyDescriptor)AnnotationTypeMemberDeclaration.NAME_PROPERTY, 
			(StructuralPropertyDescriptor)AnnotationTypeMemberDeclaration.DEFAULT_PROPERTY);
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer
			(pair);
		F<StructuralPropertyDescriptor, List<ASTNodePair>> getSubPairFunc = 
			ASTNodePair.getSubASTNodePairsFunc.f(pair);
		Buffer<ISourceChange> buffer = Buffer.empty();
		for(P2<List<ASTNodePair>, F<List<ASTNodePair>, List<ISourceChange>>> p : 
			descriptors.map(getSubPairFunc).zip(this.changeMappers).toCollection()) {
			buffer.append(p._2().f(p._1()));
		}
		container.addMultiSubChanges(buffer.toCollection());
		return container;
	}

}
