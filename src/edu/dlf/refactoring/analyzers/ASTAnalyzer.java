package edu.dlf.refactoring.analyzers;



import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.google.common.cache.LoadingCache;

import edu.dlf.refactoring.change.calculator.SimilarityASTNodeMapStrategy.IDistanceCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.IEqualityComparer;
import edu.dlf.refactoring.utils.XList;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;
import fj.data.Option;


public class ASTAnalyzer {
	private final static LoadingCache<ASTNode, String> sourceCodeRepo = 
		ServiceLocator.ResolveType(LoadingCache.class);
	private final static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private ASTAnalyzer() throws Exception {
		throw new Exception();
	}

	public static IJavaElement getJavaElement(ASTNode node) {
		CompilationUnit cu = ((CompilationUnit)node.getRoot());
		return cu.getJavaElement();
	}
	
	
	public static ASTNode parseICompilationUnit(IJavaElement icu) {
		if(icu == null)
			return null;
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
	
	public static String getOriginalSourceFromRoot(ASTNode root) {
		return sourceCodeRepo.getUnchecked(root);
	}
	
	
	public static F<Integer, F<ASTNode, List<ASTNode>>> getAncestorsFunc =
		new F<Integer, F<ASTNode,List<ASTNode>>>() {
			@Override
			public F<ASTNode, List<ASTNode>> f(final Integer kind) {
				return new F<ASTNode, List<ASTNode>>() {
					@Override
					public List<ASTNode> f(ASTNode node) {
						return FJUtils.createListFromCollection
							(getAncestors(node)).filter(new F<ASTNode, Boolean>() {
								@Override
								public Boolean f(ASTNode node) {
									return node.getNodeType() == kind;
	}});}};}};
	
	
	public static F<F<ASTNode, Boolean>, F<ASTNode, List<ASTNode>>>
		getAncestorsConditionalFunc = new F<F<ASTNode,Boolean>, F<ASTNode,List<ASTNode>>>() {
			@Override
			public F<ASTNode, List<ASTNode>> f(final F<ASTNode, Boolean> condition) {
				return new F<ASTNode, List<ASTNode>>() {
					@Override
					public List<ASTNode> f(final ASTNode node) {
						return FJUtils.createListFromCollection
							(getAncestors(node)).filter(condition);
	}};}};
	
	
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
		collectChildrenVisitor visitor = new collectChildrenVisitor(root);
		root.accept(visitor);
		return visitor.children;
	}
	
	private static class collectChildrenVisitor extends ASTVisitor
	{
		private final XList<ASTNode> children = XList.CreateList();
		private final ASTNode parent;
		
		private collectChildrenVisitor(ASTNode parent)
		{
			this.parent = parent;
		}
		
		public void preVisit(ASTNode node)
		{
			if(node.getParent() == parent)
				children.add(node);
		}
	}
	
	public static boolean areASTNodesSame(ASTNode before, ASTNode after) {
		if(before == null && after == null)
			return true;
		if(before == null || after == null)
			return false;
		String bs = DlfStringUtils.RemoveWhiteSpace(before.toString());
		String as = DlfStringUtils.RemoveWhiteSpace(after.toString());
		return as.equals(bs);
	}
	
	
	public static F2<ASTNode, ASTNode, Boolean> getASTNodeSameFunc()
	{
		return new F2<ASTNode, ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode n1, ASTNode n2) {
				return areASTNodeSame(n1, n2);
			}
		};
	}
	
	public static F2<Integer, ASTNode, List<ASTNode>> getDecendantFunc()
	{
		return new F2<Integer, ASTNode, List<ASTNode>>() {
			@Override
			public List<ASTNode> f(Integer kind, ASTNode parent) {
				Buffer<ASTNode> buffer = Buffer.empty();
				Collection<ASTNode> decs = getDecendents(parent);
				for(ASTNode node : decs) {
					if(node.getNodeType() == kind)
						buffer.snoc(node);
				}
				return buffer.toList();
			}};
	}
	
	
	
	public static IDistanceCalculator getASTNodeCompleteDistanceCalculator()
	{
		return new IDistanceCalculator(){
			@Override
			public int calculateDistance(ASTNode before, ASTNode after) {
				return DlfStringUtils.distance(DlfStringUtils.RemoveWhiteSpace(before.toString()),
						DlfStringUtils.RemoveWhiteSpace(after.toString()));
			}};
	}
	
	public static List<ASTNode> getStructuralNodeList(ASTNode node, 
		StructuralPropertyDescriptor descriptor)
	{
		java.util.List<ASTNode> nodes = (java.util.List<ASTNode>) node.
			getStructuralProperty(descriptor);
		return List.iterableList(nodes);
	}
	
	public static ASTNode getStructuralNode(ASTNode node, 
		StructuralPropertyDescriptor descriptor)
	{
		return (ASTNode) node.getStructuralProperty(descriptor);
	}
	
	
	
	public static String getMainTypeName(ASTNode node)
	{
		String pack = "";
		ASTNode root = node.getRoot();
		Object pNode = root.getStructuralProperty(CompilationUnit.PACKAGE_PROPERTY);
		if( pNode!= null) {
			pack = ((PackageDeclaration)pNode).getName().getFullyQualifiedName() + ".";
		}
		java.util.List<ASTNode> list = (java.util.List<ASTNode>) root.getStructuralProperty
			(CompilationUnit.TYPES_PROPERTY);
		if(!list.isEmpty())
		{
			TypeDeclaration type = (TypeDeclaration)list.get(0);
			return pack + type.getName().getIdentifier();
		}
		return "";
	}
	
	public static F<ASTNode, List<ASTNode>> getAllDecendantsFunc = 
		new F<ASTNode, List<ASTNode>>() {
		@Override
		public List<ASTNode> f(ASTNode root) {
			return FJUtils.createListFromCollection(getDecendents(root));
		}};
		
	public static F<ASTNode, List<ASTNode>> getChildrenFunc = 
		new F<ASTNode, List<ASTNode>>() {
		@Override
		public List<ASTNode> f(ASTNode arg0) {
			return FJUtils.createListFromCollection(getChildren(arg0));
		}};
	
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
	
	public static F2<ASTNode, ASTNode, Boolean> getMethodDeclarationNamesEqualFunc()
	{
		final F<ASTNode, String> getMDNameFunc = new F<ASTNode, String>() {
			@Override
			public String f(ASTNode md) {
				SimpleName name = (SimpleName) md.getStructuralProperty(
					MethodDeclaration.NAME_PROPERTY);
				return name.getIdentifier();
			}
		};
		return new F2<ASTNode, ASTNode, Boolean>() {
			@Override
			public Boolean f(ASTNode md1, ASTNode md2) {
				return getMDNameFunc.f(md1).equals(getMDNameFunc.f(md2));
			}
		};
	}
	
	public static boolean isStatement(ASTNode node) {
		return node instanceof Statement;
	}
	
	public static F<ASTNode, String> getContainingCompilationUnitName()
	{
		return new F<ASTNode, String>() {
			@Override
			public String f(ASTNode node) {
				return ((CompilationUnit)node.getRoot()).getJavaElement().
					getElementName();
			}
		};
	}	
	
	public static Equal<ASTNode> getASTNodeEQ()
	{
		return Equal.equal(new F<ASTNode, F<ASTNode, Boolean>>(){
			@Override
			public F<ASTNode, Boolean> f(final ASTNode n1) {
				return new F<ASTNode, Boolean>() {
					@Override
					public Boolean f(ASTNode n2) {
						return n1 == n2;
			}};}});
	}
	
	public static F2<ASTNode, ASTNode, P2<ASTNode, ASTNode>> getPNodeFunc()
	{
		return new F2<ASTNode, ASTNode, P2<ASTNode,ASTNode>>() {
			@Override
			public P2<ASTNode, ASTNode> f(ASTNode arg0, ASTNode arg1) {
				return P.p(arg0, arg1);
			}
		};
	}
	
	public static F<P2<ASTNode, ASTNode>, ASTNodePair> getP2PairConverter()
	{
		return new F<P2<ASTNode,ASTNode>, ASTNodePair>() {
			@Override
			public ASTNodePair f(P2<ASTNode, ASTNode> p) {
				return new ASTNodePair(p._1(), p._2());
			}};
	}
	
	
	public static F<ASTNodePair, P2<ASTNode, ASTNode>> getPair2PConverter()
	{
		return new F<ASTNodePair, P2<ASTNode,ASTNode>>(){
			@Override
			public P2<ASTNode, ASTNode> f(ASTNodePair pair) {
				return P.p(pair.getNodeBefore(), pair.getNodeAfter());
			}};
	}
	
	public static F<P2<ASTNode, ASTNode>, ASTNode> getP1Func = 
		new F<P2<ASTNode,ASTNode>, ASTNode>() {
		@Override
		public ASTNode f(P2<ASTNode, ASTNode> p) {
			return p._1();
	}}; 
	
	public static F<P2<ASTNode, ASTNode>, ASTNode> getP2Func = 
			new F<P2<ASTNode,ASTNode>, ASTNode>() {
			@Override
			public ASTNode f(P2<ASTNode, ASTNode> p) {
				return p._2();
	}}; 
		
	public static F<ASTNode, String> getInvokedMethodName = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			return node.getStructuralProperty(MethodInvocation.NAME_PROPERTY).
				toString();
		}
	};

	public static boolean areTypesSame(ASTNode type1, ASTNode type2) {
		IJavaElement t1 = JavaModelAnalyzer.getAssociatedITypes(type1).sort
			(JavaModelAnalyzer.getJavaElementLengthOrd()).head();
		IJavaElement t2 = JavaModelAnalyzer.getAssociatedITypes(type2).sort
			(JavaModelAnalyzer.getJavaElementLengthOrd()).head();
		return t1 == t2;
	}

}
