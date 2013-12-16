package edu.dlf.refactoring.change.calculator.body;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNodeMapperUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnumConstantDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.EnumDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class EnumDeclarationChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder enumDeclarationCB;
	private final F<ASTNodePair, ISourceChange> enumConstantCal;
	private final F<ASTNodePair, ISourceChange> simpleNameCal;
	
	@Inject
	public EnumDeclarationChangeCalculator(
		Logger logger, 
		@EnumDeclarationAnnotation String enumDeclarationLV,
		@EnumConstantDeclarationAnnotation IASTNodeChangeCalculator enumConstantCal,
		@SimpleNameAnnotation IASTNodeChangeCalculator simpleNameCal) {
			this.logger = logger;
			this.enumDeclarationCB = new ChangeBuilder(enumDeclarationLV);
			this.enumConstantCal = ASTNodePair.splitPairFunc.andThen
				(SourceChangeUtils.getChangeCalculationFunc(enumConstantCal).
					tuple());
			this.simpleNameCal = ASTNodePair.splitPairFunc.andThen
				(SourceChangeUtils.getChangeCalculationFunc(simpleNameCal).
					tuple());
	}

	private final F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> 
		constantDecMapper = ASTNodeMapperUtils.getASTNodeMapper(5, ASTNodeMapperUtils.
			getASTNodeSimilarityFunc(10, new F<ASTNode, String>() {
				@Override
				public String f(ASTNode enumConsDec) {
					return enumConsDec.getStructuralProperty(
						EnumConstantDeclaration.NAME_PROPERTY).toString();
	}}));
		
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = enumDeclarationCB.buildSimpleChange(pair);
		if(change != null)
			return change;
		SubChangeContainer container = enumDeclarationCB.createSubchangeContainer
			(pair);
		ASTNodePair simpleNamePair = pair.selectByPropertyDescriptor
			(EnumDeclaration.NAME_PROPERTY);
		container.addSubChange(simpleNameCal.f(simpleNamePair));
		P2<List<ASTNode>, List<ASTNode>> constants = pair.getSubASTNodeByDescriptor
			(EnumDeclaration.ENUM_CONSTANTS_PROPERTY);
		container.addMultiSubChanges(constantDecMapper.tuple().f(constants).map
			(ASTNodePair.createPairFunc.tuple()).map(enumConstantCal).toCollection());
		return container;
	}

}
