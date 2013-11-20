package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.ISourceChange;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;

public abstract class AbstractRefactoringImplementer implements 
	IRefactoringImplementer{

	private final Logger logger;
	private final IASTNodeChangeCalculator cuCalculator;

	protected AbstractRefactoringImplementer(
		Logger logger,
		IASTNodeChangeCalculator cuCalculator)
	{
		this.logger = logger;
		this.cuCalculator = cuCalculator;
	}
	
	protected void collectAutoRefactoringChangesAsync(Change change, 
		IAutoChangeCallback callback)
	{	
		try{
			AutoRefactoringListener listener = new AutoRefactoringListener
				(callback);
			JavaCore.addElementChangedListener(listener);
			Change undo = change.perform(new NullProgressMonitor());
			undo.perform(new NullProgressMonitor());
		}catch(Exception e)
		{
			logger.fatal(e);
		}
	}
	
	private abstract class CompilationUnitsChangedListener implements 
		IElementChangedListener {
		protected abstract void compilationUnitsChanged(List<IJavaElement> elements);
		
		private F2<Integer, IJavaElementDelta, Boolean> getFilterFunc = 
			new F2<Integer, IJavaElementDelta, Boolean>() {
			@Override
			public Boolean f(Integer flag, IJavaElementDelta delta) {
				return (delta.getFlags() & flag) != 0;
			}
		};
		
		private F<IJavaElementDelta, IJavaElement> getElementFun = 
			new F<IJavaElementDelta, IJavaElement>() {
				@Override
				public IJavaElement f(IJavaElementDelta delta) {
					return delta.getElement();
				}
			};
		
		@Override
		public void elementChanged(ElementChangedEvent event) {
			List<IJavaElementDelta> unitsDelta = searchEffectedICompilationUnitDelta
				(event.getDelta());
			List<IJavaElement> changedUnits = unitsDelta.filter(getFilterFunc.
				f(IJavaElementDelta.F_PRIMARY_RESOURCE)).map(getElementFun);
			if(changedUnits.isNotEmpty())
			{
				compilationUnitsChanged(changedUnits);
			}
		}
		
		private List<IJavaElementDelta> searchEffectedICompilationUnitDelta
			(IJavaElementDelta delta)
		{	
			F<IJavaElementDelta, List<IJavaElementDelta>> map2Children = new 
					F<IJavaElementDelta, List<IJavaElementDelta>>() {
				@Override
				public List<IJavaElementDelta> f(IJavaElementDelta delta) {
					return FunctionalJavaUtil.createListFromArray(delta.
						getAffectedChildren());
			}};

			List<IJavaElementDelta> list = List.single(delta);
			Buffer<IJavaElementDelta> buffer = Buffer.empty();
			while(list.isNotEmpty())
			{
				IJavaElementDelta head = list.head();
				list = list.append(map2Children.f(head)).drop(1);
				buffer.snoc(head);
			}
			
			return buffer.toList().filter(new F<IJavaElementDelta, Boolean>() {
				@Override
				public Boolean f(IJavaElementDelta d) {
					return d.getElement().getElementType() == IJavaElement.
						COMPILATION_UNIT;
			}});
		}
	}

	
	private class AutoRefactoringListener extends CompilationUnitsChangedListener
	{
		private List<ASTNode> unitsBefore;
		private List<ASTNode> unitsAfter;
		private final IAutoChangeCallback callback;
		private final F<IJavaElement, ASTNode> parser = new F<IJavaElement, 
			ASTNode>() {
			@Override
			public ASTNode f(IJavaElement unit) {	
				return ASTAnalyzer.parseICompilationUnit(unit);
			}
		};
		
		public AutoRefactoringListener(IAutoChangeCallback callback)
		{
			this.callback = callback;
		}
		
		@Override
		protected void compilationUnitsChanged(List<IJavaElement> elements) {
			if(unitsBefore != null)
				JavaCore.removeElementChangedListener(this);
			if(unitsBefore == null)
			{
				this.unitsBefore = elements.map(parser);
			}
			else
			{
				this.unitsAfter = elements.map(parser);
				List<ISourceChange> changes = mapCompilationUnit(unitsAfter, 
					unitsBefore).map(new F<ASTNodePair, ISourceChange>(){
						@Override
						public ISourceChange f(ASTNodePair pair) {
							return cuCalculator.CalculateASTNodeChange(pair);
				}});
				callback.onFinishChanges(changes);
			}
		}
		
		
		private List<ASTNodePair> mapCompilationUnit(List<ASTNode> list1, 
				List<ASTNode> list2)
		{
			return list1.bind(list2, new F2<ASTNode, ASTNode, P2<ASTNode, ASTNode>>(){
				@Override
				public P2<ASTNode, ASTNode> f(ASTNode n1, ASTNode n2) {
					return P.p(n1, n2);
				}}).filter(new F<P2<ASTNode,ASTNode>, Boolean>() {
					@Override
					public Boolean f(P2<ASTNode, ASTNode> arg0) {
						IJavaElement unit1 = ((CompilationUnit)arg0._1()).
							getJavaElement();
						IJavaElement unit2 = ((CompilationUnit)arg0._2()).
							getJavaElement();
						return JavaModelAnalyzer.arePathsSame(unit1, unit2);
					}
				}).map(new F<P2<ASTNode,ASTNode>, ASTNodePair>() {
					@Override
					public ASTNodePair f(P2<ASTNode, ASTNode> p) {
						return new ASTNodePair(p._1(), p._2());
					}
				});
		}
	}
}
