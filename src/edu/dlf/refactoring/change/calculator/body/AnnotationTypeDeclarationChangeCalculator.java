package edu.dlf.refactoring.change.calculator.body;

import static fj.data.List.list;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.AnnotationTypeDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.BodyDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class AnnotationTypeDeclarationChangeCalculator implements
		IASTNodeChangeCalculator {

	private final Logger logger;
	private final ChangeBuilder changeBuilder;
	private final F<ASTNodePair, ISourceChange> simpleNamCal;
	private final F<ASTNodePair, ISourceChange> bodyCal;
	private final Collection<F<ASTNodePair,List<ISourceChange>>> changeMappers;
	
	@Inject
	public AnnotationTypeDeclarationChangeCalculator(
			Logger logger,
			@AnnotationTypeDeclarationAnnotation String changeLV,
			@SimpleNameAnnotation IASTNodeChangeCalculator simpleNameCal,
			@BodyDeclarationAnnotation IASTNodeChangeCalculator bodyCal) {
		this.logger = logger;
		this.changeBuilder = new ChangeBuilder(changeLV);
		this.simpleNamCal = ASTNodePair.splitPairFunc.andThen(SourceChangeUtils.
			getChangeCalculationFunc(simpleNameCal).tuple());
		this.bodyCal = ASTNodePair.splitPairFunc.andThen(SourceChangeUtils.
			getChangeCalculationFunc(bodyCal).tuple());
		this.changeMappers = list(
			ASTNodePair.getSubASTNodePairsFunc.flip().f(AnnotationTypeDeclaration.
				NAME_PROPERTY).andThen(this.simpleNamCal.mapList()),
			ASTNodePair.getSubASTNodePairsFunc.flip().f(AnnotationTypeDeclaration.
				BODY_DECLARATIONS_PROPERTY).andThen(this.bodyCal.mapList()) 
		).toCollection();
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = this.changeBuilder.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = this.changeBuilder.createSubchangeContainer
			(pair);
		Buffer<ISourceChange> buffer = Buffer.empty();
		for(F<ASTNodePair, List<ISourceChange>> mapper : this.changeMappers) {
			buffer.append(mapper.f(pair));
		}
		container.addMultiSubChanges(buffer.toCollection());
		return container;
	}

}
