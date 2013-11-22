package edu.dlf.refactoring.change.calculator.statement;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationStatementAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SourceChangeUtils;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F2;
import fj.P2;
import fj.data.List;

public class VariableDeclarationStatementChangeCalculator implements 
	IASTNodeChangeCalculator{

	private final Logger logger;
	private final ChangeBuilder builder;
	private final IASTNodeChangeCalculator decFragCal;
	private final IASTNodeChangeCalculator typeCal;

	@Inject
	public VariableDeclarationStatementChangeCalculator(
			Logger logger,
			@VariableDeclarationStatementAnnotation String variableDecStatLV,
			@TypeAnnotation IASTNodeChangeCalculator typeCal,
			@VariableDeclarationFragmentAnnotation IASTNodeChangeCalculator decFragCal) {
		this.logger = logger;
		this.builder = new ChangeBuilder(variableDecStatLV);
		this.typeCal = typeCal;
		this.decFragCal = decFragCal;
	}
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange simpleChange = this.builder.buildSimpleChange(pair);
		if(simpleChange != null)
			return simpleChange;
		SubChangeContainer container = this.builder.createSubchangeContainer(pair);
		ASTNodePair typePair = pair.selectByPropertyDescriptor(
			VariableDeclarationStatement.TYPE_PROPERTY);
		container.addSubChange(typeCal.CalculateASTNodeChange(typePair));
		Iterable<ASTNode>[] twoLists = pair.selectChildrenByDescriptor
			(VariableDeclarationStatement.FRAGMENTS_PROPERTY);
		List<ASTNode> fragsBefore = List.iterableList(twoLists[0]);
		List<ASTNode> fragsAfter = List.iterableList(twoLists[1]);
		F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> mapper = 
			ASTAnalyzer.getASTNodeMapper(Integer.MIN_VALUE, ASTAnalyzer.
				getDefaultASTNodeSimilarityScore(10));
		container.addMultiSubChanges(mapper.f(fragsBefore, fragsAfter).
			map(SourceChangeUtils.getChangeCalculator(decFragCal).tuple()).
				toCollection());
		return container;
	}

}
