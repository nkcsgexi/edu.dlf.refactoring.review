package edu.dlf.refactoring.hiding;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.F;
import fj.Ord;
import fj.P;
import fj.P3;
import fj.data.List;
import fj.data.List.Buffer;

public class ASTUpdator extends F<ASTNode, ASTNode>{
	
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final Buffer<P3<Integer, Integer, String>> replacementBuffer = Buffer.empty();
	
	private final Effect<ASTNode> printNode = new Effect<ASTNode>() {
		@Override
		public void e(ASTNode node) {
			logger.info(node.toString());
	}};
	
	public void addNodeUpdate(ASTNode node, String replace)
	{
		replacementBuffer.snoc(P.p(node.getStartPosition(), node.getLength(), replace));
	}
	
	public void addInsertCode(int position, String code)
	{
		replacementBuffer.snoc(P.p(position, 0, code));
	}
	
	
	@Override
	public ASTNode f(ASTNode node) {
		String originalCode = ASTAnalyzer.getOriginalSourceFromRoot(node.getRoot());
		List<P3<Integer, Integer, String>> replaces = replacementBuffer.toList();
		Ord<P3<Integer, Integer, String>> order = Ord.intOrd.comap(
			new F<P3<Integer, Integer, String>, Integer>() {
			@Override
			public Integer f(P3<Integer, Integer, String> p) {
				return p._1();
		}});
		F<String, F<P3<Integer, Integer, String>, String>> folder = 
			new F<String, F<P3<Integer, Integer, String>,String>>() {
			@Override
			public F<P3<Integer, Integer, String>, String> f(final String code) {
				return new F<P3<Integer, Integer ,String>, String>() {
					@Override
					public String f(final P3<Integer, Integer, String> replace) {
						StringBuilder sb = new StringBuilder();
						int start = replace._1();
						int length = replace._2();
						sb.append(code.substring(0, start));
						sb.append(replace._3());
						sb.append(code.substring(start + length));
						return sb.toString();
		}};}};
		String code = replaces.sort(order).reverse().foldLeft(folder, originalCode);
		return ASTAnalyzer.parseICompilationUnit(code);
	}
}
