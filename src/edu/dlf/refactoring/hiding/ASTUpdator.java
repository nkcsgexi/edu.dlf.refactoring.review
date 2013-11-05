package edu.dlf.refactoring.hiding;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.F;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public class ASTUpdator extends F<ASTNode, ASTNode>{
	
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final Buffer<P2<ASTNode, String>> replacementBuffer = Buffer.empty();
	
	private final Effect<ASTNode> printNode = new Effect<ASTNode>() {
		@Override
		public void e(ASTNode node) {
			logger.info(node.toString());
	}};
	
	public void addNodeUpdate(ASTNode node, String replace)
	{
		replacementBuffer.snoc(P.p(node, replace));
	}
	
	@Override
	public ASTNode f(ASTNode node) {
		String originalCode = ASTAnalyzer.getOriginalSourceFromRoot(node.getRoot());
		List<P2<ASTNode, String>> replaces = replacementBuffer.toList();
		Ord<P2<ASTNode, String>> order = Ord.intOrd.comap(new F<P2<ASTNode,String>, 
			Integer>() {
			@Override
			public Integer f(P2<ASTNode, String> p) {
				return p._1().getStartPosition();
			}});
		F<String, F<P2<ASTNode, String>, String>> folder = 
			new F<String, F<P2<ASTNode,String>,String>>() {
			@Override
			public F<P2<ASTNode, String>, String> f(final String code) {
				return new F<P2<ASTNode,String>, String>() {
					@Override
					public String f(final P2<ASTNode, String> replace) {
						StringBuilder sb = new StringBuilder();
						int nodeStart = replace._1().getStartPosition();
						int nodeLength = replace._1().getLength();
						sb.append(code.substring(0, nodeStart));
						sb.append(replace._2());
						sb.append(code.substring(nodeStart + nodeLength));
						return sb.toString();
		}};}};
		String code = replaces.sort(order).reverse().foldLeft(folder, originalCode);
		return ASTAnalyzer.parseICompilationUnit(code);
	}
}
