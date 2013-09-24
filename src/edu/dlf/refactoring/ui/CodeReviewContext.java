package edu.dlf.refactoring.ui;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.design.IDetectedRefactoring.SingleNodeDescriptor;
import fj.Effect;
import fj.F;
import fj.data.List;

public class CodeReviewContext extends EventBus{
	
	private final Multimap<String, ICheckingResult> allResults = 
			ArrayListMultimap.create();
	private String codeAfter;
	private String codeBefore;
	
	
	@Subscribe
	public void handleResult(final ICheckingResult result)
	{
		final IDetectedRefactoring refactoring = result.getDetectedRefactoring();
		List<ASTNode> singleNodes = refactoring.
			getSingleNodeDescriptors().map(new F<SingleNodeDescriptor, ASTNode>(){
				@Override
				public ASTNode f(SingleNodeDescriptor des) {
					return refactoring.getEffectedNode(des);
				}});
		List<ASTNode> listNodes = refactoring.getNodeListDescritors().bind(new 
			F<NodeListDescriptor, List<ASTNode>>(){
			@Override
			public List<ASTNode> f(NodeListDescriptor des) {
				return refactoring.getEffectedNodeList(des);
			}});
		singleNodes.append(listNodes).map(new F<ASTNode, String>(){
			@Override
			public String f(ASTNode node) {
				return ASTAnalyzer.getMainTypeName(node);
			}}).foreach(new Effect<String>(){
				@Override
				public void e(String name) {
					allResults.put(name, result);
				}});
	}
	
	public void updateViewedCode(String codeBefore, String codeAfter)
	{
		this.codeBefore = codeBefore;
		this.codeAfter = codeAfter;
	}
	
	public void clearContext()
	{
		this.allResults.clear();
		this.codeAfter = null;
		this.codeBefore = null;
	}
	
	
}
