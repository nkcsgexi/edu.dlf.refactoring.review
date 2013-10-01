package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
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
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.UIUtils;
import fj.Effect;
import fj.F;
import fj.P2;
import fj.data.List;

public class CodeReviewUIComponent implements IFactorComponent{
	
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
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
			List<ICheckingResult> results) {
		StyledTextUpdater updater = new StyledTextUpdater();
		updater.setText(ASTAnalyzer.getOriginalSourceFromRoot(root));
		colorCorrectRefactoredNode(results, root).e(updater);
		return updater;
	}

	private StyledTextUpdater createStyledTextUpdatorBefore(ASTNode root, 
			List<ICheckingResult> results) {
		StyledTextUpdater updater = new StyledTextUpdater();
		updater.setText(ASTAnalyzer.getOriginalSourceFromRoot(root));
		colorCorrectRefactoredNode(results, root).e(updater);
		return updater;
	}
	
	
	private Effect<StyledTextUpdater> colorCorrectRefactoredNode(
			List<ICheckingResult> results, ASTNode root)
	{
		final List<P2<Integer, Integer>> ranges = getCorrectRefactoredNode
			(results, root).map(getExtractStartLengthOperation());
		return new Effect<StyledTextUpdater>() {
			@Override
			public void e(final StyledTextUpdater updater) {
				ranges.foreach(new Effect<P2<Integer, Integer>>() {	
					@Override
					public void e(P2<Integer, Integer> range) {
						updater.addStyle(UIUtils.Blue, UIUtils.CodeFont,
							range._1(), range._2());
					}
				});
			}
		};	
	}
	
	private F<P2<ASTNode, RefactoringType>, P2<Integer, Integer>> 
		getExtractStartLengthOperation()
	{
		return new F<P2<ASTNode, RefactoringType>, P2<Integer,Integer>>() {
			@Override
			public P2<Integer, Integer> f(P2<ASTNode, RefactoringType> p) {
				int start = p._1().getStartPosition();
				int length = p._1().getLength();
				return List.single(start).zip(List.single(length)).head();
			}
		};
	}
	
	private List<P2<ASTNode, RefactoringType>> getCorrectRefactoredNode(
			final List<ICheckingResult> results, 
			final ASTNode root)
	{
		return getRefactoredASTNode(results, root, new F<ICheckingResult, Boolean>(){
			@Override
			public Boolean f(ICheckingResult result) {
				return result.IsBehaviorPreserving();
			}});
	}
	
	private List<P2<ASTNode, RefactoringType>> getIncorrectRefactoredNode(
			final List<ICheckingResult> results, 
			final ASTNode root)
	{
		return getRefactoredASTNode(results, root, new F<ICheckingResult, Boolean>(){
			@Override
			public Boolean f(ICheckingResult result) {
				return !result.IsBehaviorPreserving();
			}});
	}
	
	private List<P2<ASTNode,RefactoringType>> getRefactoredASTNode(
			final List<ICheckingResult> results, 
			final ASTNode root,
			final F<ICheckingResult, Boolean> filter)
	{
		return results.filter(filter).bind(new F<ICheckingResult, 
				List<P2<ASTNode, RefactoringType>>>(){
				@Override
				public List<P2<ASTNode, RefactoringType>> f(final ICheckingResult result) {
					List<ASTNode> nodes = getAllEffectedNodes(result.
							getDetectedRefactoring()).filter(new F<ASTNode, Boolean>(){
						@Override
						public Boolean f(ASTNode node) {
							return node.getRoot() == root;
						}});
					List<RefactoringType> types = nodes.map(new F<ASTNode, RefactoringType>(){
						@Override
						public RefactoringType f(ASTNode node) {
							return result.getDetectedRefactoring().getRefactoringType();
						}});
					return nodes.zip(types);
				}});
	}
	
	
	private List<ASTNode> getAllEffectedNodes(final IDetectedRefactoring refactoring)
	{
		return refactoring.getNodeListDescritors().bind(new F<NodeListDescriptor, 
				List<ASTNode>>(){
			@Override
			public List<ASTNode> f(NodeListDescriptor d) {
				return refactoring.getEffectedNodeList(d);
			}}).append(refactoring.getSingleNodeDescriptors().map(new 
					F<SingleNodeDescriptor, ASTNode>(){
				@Override
				public ASTNode f(SingleNodeDescriptor d) {
					return refactoring.getEffectedNode(d);
				}}));
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
