package edu.dlf.refactoring.analyzers;



import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.IEqualityComparer;
import edu.dlf.refactoring.utils.XList;
import fj.F;
import fj.data.Option;


public class ASTAnalyzer {
	
	private static HashMap<ASTNode, String> sourceCodeRepo =
			new HashMap<ASTNode, String>();

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private ASTAnalyzer() throws Exception {
		throw new Exception();
	}

	public static ASTNode parseICompilationUnit(IJavaElement icu) {
		try {
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			String source;
			source = ((ICompilationUnit) icu).getSource();
			parser.setSource((ICompilationUnit) icu);
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			ASTNode root = parser.createAST(null);
			sourceCodeRepo.put(root, source);
			return root;
		} catch (Exception e) {
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource((ICompilationUnit) icu);
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			ASTNode root = parser.createAST(null);
			return root;
		}
	}

	
	public static ASTNode parseICompilationUnit(String code) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(code.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		ASTNode root = parser.createAST(null);
		sourceCodeRepo.put(root, code);
		return root;
	}
	
	
	public static ASTNode parseStatements(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_STATEMENTS);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		ASTNode root = parser.createAST(null);
		sourceCodeRepo.put(root, source);
		return root;
	}

	public static ASTNode parseExpression(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setSource(source.toCharArray());
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		ASTNode root = parser.createAST(null);
		sourceCodeRepo.put(root, source);
		return root;
	}
	
	public static String getOriginalSourceFromRoot(ASTNode root)
	{
		return sourceCodeRepo.get(root);
	}
	
	
	public static XList<ASTNode> getAncestors(ASTNode node)
	{
		XList<ASTNode> results = XList.CreateList();
		for(;node != null;)
		{
			results.add(node);
			node = node.getParent();
		}
		return results;
	}
	
	public static Option<ASTNode> getAncestor(ASTNode node, F<ASTNode, Boolean> 
		predicate)
	{
		while(node != null && predicate.f(node) != true)
		{
			node = node.getParent();
		}
		return Option.fromNull(node);
	}
	
	
	public static XList<ASTNode> getDecendents(ASTNode root)
	{
		XList<ASTNode> unhandledNodes = new XList<ASTNode>(root);
		XList<ASTNode> results = new XList<ASTNode>();
		
		for(;unhandledNodes.any();)
		{
			ASTNode first = unhandledNodes.remove(0);
			XList<ASTNode> children = getChildren(first);
			results.addAll(children);
			unhandledNodes.addAll(children);
		}
		return results;
	}
	
	public static XList<ASTNode> getChildren(ASTNode root)
	{
		XList<ASTNode> allChildren = XList.CreateList();
	    List list= root.structuralPropertiesForType();
	    for (int i= 0; i < list.size(); i++) {
	        StructuralPropertyDescriptor curr= (StructuralPropertyDescriptor) list.get(i);
            Object child = root.getStructuralProperty(curr);
	        if (child instanceof List) {
	        	for(Object c : (List)child)
	        	{
	        		if(c instanceof ASTNode)
	        		{
	        			allChildren.add((ASTNode)c);
	        		}
	        	}
	        } else if (child instanceof ASTNode) {
	            allChildren.add((ASTNode)child);
            }
	    }
	    return allChildren;
	}
	
	public static boolean areASTNodesSame(ASTNode before, ASTNode after) {
		
		if(before == null && after == null)
			return true;
		if(before == null || after == null)
			return false;
		String bs = XStringUtils.RemoveWhiteSpace(before.toString());
		String as = XStringUtils.RemoveWhiteSpace(after.toString());
		return as.equals(bs);
	}
	
	
	public static IDistanceCalculator getASTNodeCompleteDistanceCalculator()
	{
		return new IDistanceCalculator(){
			@Override
			public int calculateDistance(ASTNode before, ASTNode after) {
				return XStringUtils.distance(XStringUtils.RemoveWhiteSpace(before.toString()),
						XStringUtils.RemoveWhiteSpace(after.toString()));
			}};
	}
	
	public static String getMainTypeName(ASTNode node)
	{
		String pack = "";
		ASTNode root = node.getRoot();
		Object pNode = root.getStructuralProperty(CompilationUnit.PACKAGE_PROPERTY);
		if( pNode!= null) {
			pack = ((PackageDeclaration)pNode).getName().getFullyQualifiedName() + ".";
		}
		List<ASTNode> list = (List<ASTNode>) root.getStructuralProperty
				(CompilationUnit.TYPES_PROPERTY);
		if(!list.isEmpty())
		{
			TypeDeclaration type = (TypeDeclaration)list.get(0);
			return pack + type.getName().getIdentifier();
		}
		return "";
	}
	
	
	public static boolean areASTNodeSame (ASTNode node1, ASTNode node2)
	{
		return node1.subtreeMatch(new ASTMatcher(), node2);
	}
	
	public static IEqualityComparer<ASTNode> getASTEqualityComparer()
	{
		return new IEqualityComparer<ASTNode>(){
			@Override
			public boolean AreEqual(ASTNode a, ASTNode b) {
				return areASTNodesSame(a, b);
			}};
	}
	
	
	public static boolean isStatement(ASTNode node) {
		return node instanceof Statement;
	}

	public static Boolean areNodesNeighbors(ASTNode node1, ASTNode node2) {
		
		
		return null;
	}

}
