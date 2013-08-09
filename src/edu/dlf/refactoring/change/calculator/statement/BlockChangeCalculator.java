package edu.dlf.refactoring.change.calculator.statement;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;

import com.google.common.base.Function;
import com.google.inject.Inject;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Delta.TYPE;
import edu.dlf.refactoring.analyzers.XStringUtils;
import edu.dlf.refactoring.change.ASTAnnotations.BlockAnnotation;
import edu.dlf.refactoring.change.ASTAnnotations.StatementAnnotation;
import edu.dlf.refactoring.change.ChangeBuilder;
import edu.dlf.refactoring.change.IASTNodeChangeCalculator;
import edu.dlf.refactoring.change.SubChangeContainer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XList;

public class BlockChangeCalculator implements IASTNodeChangeCalculator{

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final IASTNodeChangeCalculator stCalculator;
	private final ChangeBuilder changeBuilder;


	@Inject
	public BlockChangeCalculator(
			@BlockAnnotation String changeLevel,
			@StatementAnnotation IASTNodeChangeCalculator stCalculator)
	{
		this.changeBuilder = new ChangeBuilder(changeLevel);
		this.stCalculator = stCalculator;
	}
	
	
	@Override
	public ISourceChange CalculateASTNodeChange(ASTNodePair pair) {
		
		try{
			ISourceChange change = changeBuilder.buildSimpleChange(pair);
			if(change != null)
				return change;
			SubChangeContainer container = changeBuilder.createSubchangeContainer();
			XList<ASTNode> beforeSts = new XList(((Block)pair.getNodeBefore()).statements());
			XList<ASTNode> afterSts =new XList(((Block)pair.getNodeAfter()).statements());
			
			Function<String, String> spaceRemover = new Function<String, String>(){
				@Override
				public String apply(String text) {
					return XStringUtils.RemoveWhiteSpace(text);
			}};
			
			XList<String> beforeLines = beforeSts.cast(String.class);
			beforeLines = beforeLines.select(spaceRemover);
			XList<String> afterLines = afterSts.cast(String.class);
			afterLines = afterLines.select(spaceRemover);
			
			List<Delta> diffs = DiffUtils.diff(beforeLines, afterLines).getDeltas();
			for(Delta diff : diffs)
			{
				if(diff.getType() == TYPE.CHANGE)
				{
					int changeCount = Math.min(diff.getOriginal().getLines().size(), diff.
							getRevised().getLines().size());
					for(int i = 0; i < changeCount; i++)
					{
						container.addSubChange(stCalculator.CalculateASTNodeChange(new ASTNodePair(
							beforeSts.get(diff.getOriginal().getPosition() + i),
							afterSts.get(diff.getRevised().getPosition() + i)	
						)));
					}
					
					if(diff.getOriginal().getLines().size() > changeCount)
					{
						container.addMultiSubChanges(CreateRemoveStatements(beforeSts.subList(changeCount, 
								beforeSts.size() - 1)));
					}
					
					if(diff.getRevised().getLines().size() > changeCount)
					{
						container.addMultiSubChanges(CreateAddStatements(afterSts.subList(changeCount, 
								afterSts.size() - 1)));
					}
					
				}
				else if(diff.getType() == TYPE.DELETE)
				{
					int start = diff.getOriginal().getPosition();
					int end = start + diff.getOriginal().size() - 1;
					container.addMultiSubChanges(this.CreateRemoveStatements(beforeSts.subList(start, end)));
					
				} else if(diff.getType() == TYPE.INSERT)
				{
					int start = diff.getRevised().getPosition();
					int end = start + diff.getRevised().size() - 1;
					container.addMultiSubChanges(this.CreateAddStatements(afterSts.subList(start, end)));
				}
			}
			return container;
		}catch(Exception e)
		{
			logger.fatal(e);
			return this.changeBuilder.createUnknownChange(pair);
		}
	}


	private Collection<ISourceChange> CreateRemoveStatements(List<ASTNode> stats) {
		XList<ISourceChange> result = new XList<ISourceChange>();
		for(ASTNode st : stats){
			result.add(stCalculator.CalculateASTNodeChange(new ASTNodePair(st, null)));
		}
		return result;
	}


	private Collection<ISourceChange> CreateAddStatements(List<ASTNode> stats) {
		XList<ISourceChange>  result = new XList<ISourceChange>();
		for(ASTNode st : stats) {
			result.add(stCalculator.CalculateASTNodeChange(new ASTNodePair(null, st)));
		}
		return result;
	}

}
