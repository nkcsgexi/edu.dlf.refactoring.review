package edu.dlf.refactoring.ui;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.design.IDetectedRefactoring.SingleNodeDescriptor;
import edu.dlf.refactoring.design.IFactorComponent;
import fj.Effect;
import fj.F;
import fj.data.List;

public class CodeReviewUIComponent implements IFactorComponent{
	
	private final Multimap<String, ICheckingResult> allResults = 
			ArrayListMultimap.create();
	private final EventBus bus = new EventBus();
	private ASTNode rootAfter;
	private ASTNode rootBefore;
	
	public void updateViewedCode(ASTNode rootBefore, ASTNode rootAfter)
	{
		this.rootBefore = rootBefore;
		this.rootAfter = rootAfter;
		String mainTypeName = ASTAnalyzer.getMainTypeName(rootBefore);
		StyledTextUpdater[] updators = new StyledTextUpdater[]{ 
			createStyledTextUpdatorBefore(this.rootBefore, 
				List.iterableList(allResults.get(mainTypeName))),
			createStyledTextUpdatorAfter(this.rootAfter, List.iterableList
				(allResults.get(mainTypeName)))};
		this.bus.post(updators);
	}
	
	private StyledTextUpdater createStyledTextUpdatorAfter(ASTNode rootBefore2, 
			List<ICheckingResult> collection) {
		
		
		return null;
	}

	private StyledTextUpdater createStyledTextUpdatorBefore(ASTNode rootBefore2, 
			List<ICheckingResult> collection) {
		
		
		return null;
	}

	public void clearContext()
	{
		this.allResults.clear();
		this.rootBefore = null;
		this.rootAfter = null;
	}

	@Override
	public Void listen(Object event) {		
		if(event instanceof ICheckingResult) {
			final ICheckingResult result = (ICheckingResult) event;
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
		return null;
	}

	@Override
	public Void registerListener(Object listener) {
		bus.register(listener);
		return null;
	}
	
	
}
