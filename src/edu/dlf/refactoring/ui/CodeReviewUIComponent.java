package edu.dlf.refactoring.ui;

import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.design.IDetectedRefactoring.SingleNodeDescriptor;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.utils.UIUtils;
import fj.Effect;
import fj.F;
import fj.data.List;

public class CodeReviewUIComponent implements IFactorComponent{
	
	private final Multimap<String, ICheckingResult> allResults = 
			ArrayListMultimap.create();
	private final EventBus bus = new EventBus();
	
	@Inject
	public CodeReviewUIComponent()
	{
	}
	
	public synchronized void updateViewedCode(ASTNode rootBefore, ASTNode rootAfter)
	{
		String mainTypeName = ASTAnalyzer.getMainTypeName(rootBefore);
		StyledTextUpdater[] updators = new StyledTextUpdater[]{ 
			createStyledTextUpdatorBefore(rootBefore, 
				List.iterableList(allResults.get(mainTypeName))),
			createStyledTextUpdatorAfter(rootAfter, List.iterableList
				(allResults.get(mainTypeName)))};
		this.bus.post(updators);
	}
	
	private StyledTextUpdater createStyledTextUpdatorAfter(ASTNode root, 
			List<ICheckingResult> collection) {
		StyledTextUpdater updater = new StyledTextUpdater();
		updater.addText(root.toString(), UIUtils.Black, UIUtils.CodeFont);
		return updater;
	}

	private StyledTextUpdater createStyledTextUpdatorBefore(ASTNode root, 
			List<ICheckingResult> collection) {
		StyledTextUpdater updater = new StyledTextUpdater();
		updater.addText(root.toString(), UIUtils.Black, UIUtils.CodeFont);
		return updater;
	}

	public synchronized void clearContext()
	{
		this.allResults.clear();
	}

	@Override
	public synchronized Void listen(Object event) {
		
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
	public synchronized Void registerListener(ICompListener listener) {
		bus.register(listener);
		return null;
	}
	
	
}
