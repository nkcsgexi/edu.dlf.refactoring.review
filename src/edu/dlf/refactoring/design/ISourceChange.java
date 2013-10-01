package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

public interface ISourceChange extends IASTNodePair, IJavaElementPair{
	boolean hasSubChanges();
	ISourceChange[] getSubSourceChanges();
	SourceChangeType getSourceChangeType();
	ISourceChange getParentChange();
	String getSourceChangeLevel();
	
	public abstract class AbstractSourceChange implements ISourceChange
	{
		
		private final ASTNode nodeBefore;
		private final ASTNode nodeAfter;
		private final String changeLevel;
		private final IJavaElement elementAfter;
		private final IJavaElement elementBefore;
		private ISourceChange parent;
		
		
		protected AbstractSourceChange(String changeLevel, ASTNode nodeBefore,
				ASTNode nodeAfter)
		{
			this.changeLevel = changeLevel;
			this.nodeBefore = nodeBefore;
			this.nodeAfter = nodeAfter;
			this.elementBefore = null;
			this.elementAfter = null;
		}
		
		protected AbstractSourceChange(String changeLevel, IJavaElement elementBefore,
				IJavaElement elementAfter)
		{
			this.changeLevel = changeLevel;
			this.elementBefore = elementBefore;
			this.elementAfter = elementAfter;
			this.nodeAfter = null;
			this.nodeBefore = null;
		}
		
		protected AbstractSourceChange(String changeLevel)
		{
			this.changeLevel = changeLevel;
			this.elementBefore = null;
			this.elementAfter = null;
			this.nodeAfter = null;
			this.nodeBefore = null;
		}
		
		
		@Override
		public final String getSourceChangeLevel()
		{
			return this.changeLevel;
		}
		
		@Override
		public final ISourceChange getParentChange() {
			return this.parent;
		}
		
		@Override
		public final ASTNode getNodeBefore()
		{
			return this.nodeBefore;
		}
		
		@Override
		public final ASTNode getNodeAfter()
		{
			return nodeAfter;
		}
		
		@Override
		public final IJavaElement getElementBefore()
		{
			return this.elementBefore;
		}
		
		@Override
		public final IJavaElement getElementAfter()
		{
			return this.elementAfter;
		}
		
		
		public final void setParentChange(ISourceChange parent) {
			this.parent = parent;
		}
	}
	
	
	
	public enum SourceChangeType
	{
		ADD,
		REMOVE,
		UPDATE,
		PARENT,
		UNKNOWN,
		NULL, 
	}
}
