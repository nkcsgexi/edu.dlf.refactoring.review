package edu.dlf.refactoring.design;

import org.eclipse.jdt.core.dom.ASTNode;

public interface ISourceChange extends IASTNodePair{
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
		private ISourceChange parent;
		
		
		protected AbstractSourceChange(String changeLevel, ASTNode nodeBefore,
				ASTNode nodeAfter)
		{
			this.changeLevel = changeLevel;
			this.nodeBefore = nodeBefore;
			this.nodeAfter = nodeAfter;
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
