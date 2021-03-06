package edu.dlf.refactoring.utils;


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
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;
import fj.data.Option;

public class RefactoringUtils {

	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	public static Option<Change> createChange(Refactoring ref)
	{
		try{
			RefactoringStatus status = ref.checkAllConditions(new 
				NullProgressMonitor());
			if(status.isOK()){
				return Option.some(ref.createChange(new NullProgressMonitor()));
			} else
			{
				logger.fatal(ref.getName() + ":" + status.getEntries()[0].
					getMessage());
				throw new Exception();
			}
		}catch(Exception e)
		{
			logger.fatal("Check refactoring condition fails: " + e);
			return Option.none();
		}
	}
	
	public static Change performChange(Change change)
	{
		try {
			return change.perform(new NullProgressMonitor());
		} catch (Exception e) {
			logger.fatal(e);
			return null;
		}
	}
	
	
	
	public synchronized static List<ASTNodePair> collectChangedCompilationUnits
		(Change change)
	{	
		try{
			WaiterNotifier waiter = new WaiterNotifier();
			AutoRefactoringListener listener = new AutoRefactoringListener(waiter);
			JavaCore.addElementChangedListener(listener);
			Change undo = change.perform(new NullProgressMonitor());
			waiter.doWait();
			JavaCore.removeElementChangedListener(listener);
			waiter = new WaiterNotifier();
			AutoRefactoringListener fListener = new AutoRefactoringListener(waiter);
			JavaCore.addElementChangedListener(fListener);
			undo.perform(new NullProgressMonitor());
			waiter.doWait();
			JavaCore.removeElementChangedListener(fListener);
			return mapCompilationUnit(fListener.getAffectedCompilationUnits(), 
				listener.getAffectedCompilationUnits());
		}catch(Exception e)
		{
			logger.fatal(e);
			return List.nil();
		}
	}
	
	
	
	private static List<ASTNodePair> mapCompilationUnit(List<ASTNode> list1, 
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
	
	private static class AutoRefactoringListener implements IElementChangedListener
	{
		private List<ASTNode> units;
		private final WaiterNotifier waiter;
		
		public AutoRefactoringListener(WaiterNotifier waiter) {
			this.waiter = waiter;
		}

		@Override
		public void elementChanged(ElementChangedEvent event) {
			if(event.getType() != ElementChangedEvent.POST_CHANGE)
				return;
			List<IJavaElement> changedIU = List.nil();
			IJavaElementDelta delta = event.getDelta();
			if(delta.getElement().getElementType() == IJavaElement.
				COMPILATION_UNIT)
			{
				changedIU = changedIU.snoc(delta.getElement());
			} else if(delta.getElement().getAncestor(IJavaElement.
					COMPILATION_UNIT) != null)
			{
				changedIU = changedIU.snoc(delta.getElement().getAncestor(
					IJavaElement.COMPILATION_UNIT));
				
			} else 
			{
				changedIU = searchEffectedICompilationUnit(delta);
			}
			
			F<IJavaElement, ASTNode> parser = new F<IJavaElement, ASTNode>() {
				@Override
				public ASTNode f(IJavaElement unit) {	
					return ASTAnalyzer.parseICompilationUnit(unit);
				}
			};
			this.units = changedIU.map(parser);		
			this.waiter.doNotify();
		}
		
		private List<IJavaElement> searchEffectedICompilationUnit(IJavaElementDelta 
			delta)
		{						
			F<IJavaElementDelta, List<IJavaElementDelta>> map2Children = new 
					F<IJavaElementDelta, List<IJavaElementDelta>>() {
				@Override
				public List<IJavaElementDelta> f(IJavaElementDelta delta) {
					Buffer<IJavaElementDelta> children = Buffer.empty();
					for(IJavaElementDelta child : delta.getAffectedChildren())
					{
						children.snoc(child);
					}
					return children.toList();
				}
			};

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
				}
			}).map(new F<IJavaElementDelta, IJavaElement>() {
				@Override
				public IJavaElement f(IJavaElementDelta d) {
					return d.getElement();
				}});
		}
		
		public List<ASTNode> getAffectedCompilationUnits()
		{
			return this.units;
		}
	}
}
