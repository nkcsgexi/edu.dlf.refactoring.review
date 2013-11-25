package edu.dlf.refactoring.ui;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.swt.graphics.Color;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.checkers.RefactoringCheckerUtils;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.design.IDetectedRefactoring.SingleNodeDescriptor;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.design.ServiceLocator.HidingCompAnnotation;
import edu.dlf.refactoring.hiding.IHidingComponentInput;
import edu.dlf.refactoring.utils.UIUtils;
import fj.Effect;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.P3;
import fj.data.List;

public class CodeReviewUIComponent implements IFactorComponent{
	
	private final EventBus bus = new EventBus();
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final Multimap<String, ICheckingResult> allResults = 
			ArrayListMultimap.create();
	private final Hashtable<ICheckingResult, Color> colorRepo = new 
		Hashtable<ICheckingResult, Color>();
	private final IFactorComponent hidingComponent;
	
	private final F<ICheckingResult, Color> getColorFunc = 
		new F<ICheckingResult, Color>() {
			@Override
			public Color f(ICheckingResult result) {
				if(!colorRepo.containsKey(result))
				{
					colorRepo.put(result, UIUtils.getNextColor());
				}
				return colorRepo.get(result);
		}};
	
	@Inject
	public CodeReviewUIComponent(@HidingCompAnnotation IFactorComponent hidingComponent)
	{
		this.hidingComponent = hidingComponent;
	/*	this.registerListener((ICompListener)ServiceLocator.ResolveType
			(RefactoringComparator.class));
	*/}
	
	public synchronized void updateViewedCode(ASTNode rootBefore, ASTNode rootAfter)
	{
		String mainTypeName = ASTAnalyzer.getMainTypeName(rootBefore);
		StyledTextUpdater[] updators = new StyledTextUpdater[]{ 
			createStyledTextUpdatorBefore(rootBefore, 
				List.iterableList(allResults.get(mainTypeName))), null,
			createStyledTextUpdatorAfter(rootAfter, List.iterableList
				(allResults.get(mainTypeName)))};
		updateViewHidingRefactoring(rootAfter, List.iterableList
			(allResults.get(mainTypeName)), updators);
	}
	
	private void updateViewHidingRefactoring(final ASTNode root, final List
		<ICheckingResult> results, final StyledTextUpdater[] updators)
	{
		this.hidingComponent.listen(new IHidingComponentInput() {
			@Override
			public ASTNode getRootNode() {
				return root;
			}
			
			@Override
			public List<IDetectedRefactoring> getHideRefactorings() {
				return results.filter(new F<ICheckingResult, Boolean>() {
					@Override
					public Boolean f(ICheckingResult result) {
						return result.IsBehaviorPreserving();
					}}).map(new F<ICheckingResult, IDetectedRefactoring>() {
						@Override
						public IDetectedRefactoring f(ICheckingResult result) {
							return result.getDetectedRefactoring();
						}});
			}
			
			@Override
			public void callback(ASTNode rootAfterHiding) {
				StyledTextUpdater updater = new StyledTextUpdater();
				updater.setText(ASTAnalyzer.getOriginalSourceFromRoot
					(rootAfterHiding));
				updators[1] = updater;
				bus.post(updators);
			}});
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
		final List<P3<Integer, Integer, Color>> ranges = getCorrectRefactoredNode
			(results, root).map(getExtractStartLengthOperation());
		return new Effect<StyledTextUpdater>() {
			@Override
			public void e(final StyledTextUpdater updater) {
				ranges.foreach(new Effect<P3<Integer, Integer, Color>>() {	
					@Override
					public void e(P3<Integer, Integer, Color> range) {
						updater.addStyle(range._3(), UIUtils.CodeFont,
							range._1(), range._2());
					}
				});}};	
	}
	
	private F<P2<ICheckingResult,ASTNode>, P3<Integer,Integer, Color>> 
		getExtractStartLengthOperation()
	{
		return new F<P2<ICheckingResult, ASTNode>, P3<Integer,Integer, Color>>() {
			@Override
			public P3<Integer, Integer, Color> f(P2<ICheckingResult, ASTNode> p) {
				int start = p._2().getStartPosition();
				int length = p._2().getLength();
				Color color = getColorFunc.f(p._1());
				return P.p(start, length, color);
		}};
	}
	
	private List<P2<ICheckingResult, ASTNode>> getCorrectRefactoredNode(
			final List<ICheckingResult> results, 
			final ASTNode root)
	{
		return getRefactoredASTNode(results, root, new F<ICheckingResult, Boolean>(){
			@Override
			public Boolean f(ICheckingResult result) {
				return result.IsBehaviorPreserving();
			}});
	}
	
	private List<P2<ICheckingResult, ASTNode>> getIncorrectRefactoredNode(
			final List<ICheckingResult> results, 
			final ASTNode root)
	{
		return getRefactoredASTNode(results, root, new F<ICheckingResult, Boolean>(){
			@Override
			public Boolean f(ICheckingResult result) {
				return !result.IsBehaviorPreserving();
			}});
	}
	
	private List<P2<ICheckingResult, ASTNode>> getRefactoredASTNode(
			final List<ICheckingResult> results, 
			final ASTNode root,
			final F<ICheckingResult, Boolean> filter)
	{
		return results.filter(filter).bind(new F<ICheckingResult, 
				List<P2<ICheckingResult, ASTNode>>>(){
				@Override
				public List<P2<ICheckingResult, ASTNode>> 
					f(final ICheckingResult result) {
					List<ASTNode> nodes = RefactoringCheckerUtils.getAllEffectedNodes(result.
							getDetectedRefactoring()).filter(new F<ASTNode, Boolean>(){
						@Override
						public Boolean f(ASTNode node) {
							return node.getRoot() == root;
						}});
					return List.single(result).bind(nodes, new F2<ICheckingResult,
						ASTNode, P2<ICheckingResult, ASTNode>>(){
							@Override
							public P2<ICheckingResult, ASTNode> f(
									ICheckingResult result, ASTNode node) {
								return P.p(result, node);
							}});}});
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
