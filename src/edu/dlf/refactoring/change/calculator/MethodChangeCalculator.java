package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.google.common.base.Function;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ASTAnnotations.BlockAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.MethodDeclarationAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.SimpleNameAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.VariableDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class MethodChangeCalculator implements IASTNodeChangeCalculator {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator blCalculator;
	private final ChangeBuilder changeBuilder;
	private final IASTNodeChangeCalculator snCalculator;
	private final IASTNodeMapStrategy mapStrategy;
	private final IASTNodeChangeCalculator vdCalculator;

	public MethodChangeCalculator(
			@MethodDeclarationAnnotation String changeLevel,
			IASTNodeMapStrategy mapStrategy,
			@SimpleNameAnnotation IASTNodeChangeCalculator snCalculator,
			@VariableDeclarationAnnotation IASTNodeChangeCalculator vdCalculator,
			@BlockAnnotation IASTNodeChangeCalculator blCalculator) {
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.snCalculator = snCalculator;
		this.blCalculator = blCalculator;
		this.vdCalculator = vdCalculator;
		this.mapStrategy = mapStrategy;
	}

	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {

		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if (change != null)
			return change;

		try {
			SubChangeContainer container = changeBuilder.createSubchangeContainer();
			MethodDeclaration methodBefore = (MethodDeclaration) pair.getNodeBefore();
			MethodDeclaration methodAfter = (MethodDeclaration) pair.getNodeBefore();

			container.addSubChange(snCalculator
				.CalculateASTNodeChange(new ASTNodePair(methodBefore
					.getName(), methodAfter.getName())));

			XList<ASTNode> pBefore = new XList<ASTNode>(methodBefore.parameters());
			XList<ASTNode> pAfter = new XList<ASTNode>(methodAfter.parameters());

			XList<ASTNode> pnBefore = pBefore.intersect(pAfter,
					ASTAnalyzer.getASTEqualityComparer());
			XList<ASTNode> pnAfter = pAfter.intersect(pBefore,
					ASTAnalyzer.getASTEqualityComparer());

			container.addMultiSubChanges(this.mapStrategy.map(pnBefore, pnAfter).select(
					new Function<ASTNodePair, ISourceChange>() {
						@Override
						public ISourceChange apply(ASTNodePair pair) {
							return vdCalculator.CalculateASTNodeChange(pair);
						}
					}));

			container.addSubChange(blCalculator
					.CalculateASTNodeChange(new ASTNodePair(methodBefore
							.getBody(), methodAfter.getBody())));
			return container;
		} catch (Exception e) {
			logger.fatal(e);
			return changeBuilder.createUnknownChange(pair);
		}
	}

}
