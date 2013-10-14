package edu.dlf.refactoring.change.calculator;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.ChangeComponentInjector.FieldDeclarationAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.TypeAnnotation;
import edu.dlf.refactoring.change.ChangeComponentInjector.VariableDeclarationFragmentAnnotation;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;

public class FieldDeclarationCalculator implements IASTNodeChangeCalculator{
	
	private final Logger logger;
	private final IASTNodeChangeCalculator typeCalculator;
	private final IASTNodeChangeCalculator vdfCalculator;
	private final ChangeBuilder changeBuilder;

	@Inject
	public FieldDeclarationCalculator(
			Logger logger,
			@FieldDeclarationAnnotation String changeLevel,
			@TypeAnnotation IASTNodeChangeCalculator typeCalculator,
			@VariableDeclarationFragmentAnnotation IASTNodeChangeCalculator 
				vdfCalculator)
	{
		this.logger = logger;
		this.typeCalculator = typeCalculator;
		this.vdfCalculator = vdfCalculator;
		this.changeBuilder = new ChangeBuilder(changeLevel);
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		ISourceChange change = changeBuilder.buildSimpleChange(pair);
		if (change != null)
			return change;
		SubChangeContainer container = changeBuilder.createSubchangeContainer(pair);
		container.addSubChange(typeCalculator.CalculateASTNodeChange(pair.
			selectByPropertyDescriptor(FieldDeclaration.TYPE_PROPERTY)));
		F<ASTNode, List<ASTNode>> getVDF = new F<ASTNode, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(ASTNode field) {
				return ASTAnalyzer.getStructuralNodeList(field, FieldDeclaration.
					FRAGMENTS_PROPERTY);
			}
		};
		F2<List<ASTNode>, List<ASTNode>, List<P2<ASTNode, ASTNode>>> mapper = 
			ASTAnalyzer.getASTNodeMapper(Integer.MIN_VALUE, 
				new F2<ASTNode, ASTNode, Integer>() {
			@Override
			public Integer f(ASTNode n1, ASTNode n2) {
				return 0 - XStringUtils.distance(n1.toString(), n2.toString());
			}
		});
		
		container.addMultiSubChanges(mapper.f(getVDF.f(pair.getNodeBefore()),
			getVDF.f(pair.getNodeAfter())).
			map(new F<P2<ASTNode,ASTNode>, ISourceChange>() {
				@Override
				public ISourceChange f(P2<ASTNode, ASTNode> p) {
					return vdfCalculator.CalculateASTNodeChange(new ASTNodePair
						(p._1(), p._2()));
				}}).toCollection());
		return container;
	}

}
