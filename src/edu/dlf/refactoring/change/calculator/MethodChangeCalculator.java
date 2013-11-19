package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import com.google.common.base.Function;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.BlockAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.SingleVariableDeclarationAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;
import fj.F;

public class MethodChangeCalculator implements IASTNodeChangeCalculator {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator blCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snCalculator;
	private final IASTNodeChangeCalculator svdCalculator;

	private final F<ASTNode, String> getSingleVariableName = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode dec) {
			return dec.getStructuralProperty(SingleVariableDeclaration.NAME_PROPERTY).
				toString();
	}}; 
	
	
	@Inject
	public MethodChangeCalculator(
			@MethodDeclarationAnnotation String changeLevel,
			@SimpleNameAnnotation IASTNodeChangeCalculator snCalculator,
			@SingleVariableDeclarationAnnotation IASTNodeChangeCalculator svdCalculator,
			@BlockAnnotation IASTNodeChangeCalculator blCalculator) {
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.snCalculator = snCalculator;
		this.blCalculator = blCalculator;
		this.svdCalculator = svdCalculator;
	}

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		logger.debug("Compare methods: " + pair.getCompareInfor(ASTNode2StringUtils.
			getMethodNameFunc));
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if (change != null)
			return change;

		try {
			SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
			MethodDeclaration methodBefore = (MethodDeclaration) pair.getNodeBefore();
			MethodDeclaration methodAfter = (MethodDeclaration) pair.getNodeAfter();

			container.addSubChange(snCalculator
				.CalculateASTNodeChange(new ASTNodePair(methodBefore
					.getName(), methodAfter.getName())));

			XList<ASTNode> pBefore = new XList<ASTNode>(methodBefore.parameters());
			XList<ASTNode> pAfter = new XList<ASTNode>(methodAfter.parameters());
			
			IASTNodeMapStrategy strategy = createMapStrategy();
			container.addMultiSubChanges(strategy.map(pBefore, pAfter).select(
				new Function<ASTNodePair, ISourceChange>() {
					@Override
					public ISourceChange apply(ASTNodePair pair) {
						return svdCalculator.CalculateASTNodeChange(pair);
			}}));

			container.addSubChange(blCalculator
				.CalculateASTNodeChange(new ASTNodePair(methodBefore
					.getBody(), methodAfter.getBody())));
			return container;
		} catch (Exception e) {
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}

	private IASTNodeMapStrategy createMapStrategy() {
		return new SimilarityASTNodeMapStrategy(new IDistanceCalculator(){
			@Override
			public int calculateDistance(ASTNode before, ASTNode after) {
				return XStringUtils.distance(getSingleVariableName.f(before), 
					getSingleVariableName.f(after));
		}});
	}
}
