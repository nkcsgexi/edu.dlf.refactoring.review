package edu.dlf.refactoring.change;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.analyzers.FunctionalJavaUtil;
import edu.dlf.refactoring.change.ChangeComponentInjector.CompilationUnitAnnotation;
import edu.dlf.refactoring.design.ISourceChange;
import fj.Effect;
import fj.F;
import fj.F2;
import fj.data.List;

public class ChangedLinesComputer {

	private final Logger logger;
	private final String cuLevel;
	private int[] result;

	@Inject
	public ChangedLinesComputer(Logger logger, 
			@CompilationUnitAnnotation String cuLevel) {
		this.logger = logger;
		this.cuLevel = cuLevel;
	}
	
	private F<ISourceChange, List<int[]>> getChangedLines = 
		new F<ISourceChange, List<int[]>>() {
		@Override
		public List<int[]> f(ISourceChange change) {
			return SourceChangeUtils.getSelfAndDescendent(change).filter(
				new F<ISourceChange, Boolean>() {
				@Override
				public Boolean f(ISourceChange change) {
					return change.getSourceChangeLevel().equals(cuLevel);
			}}).map(getChangedLineInCompilationUnit);
	}}; 
	
	private F<ASTNode, String> getSource = new F<ASTNode, String>() {
		@Override
		public String f(ASTNode node) {
			return node == null ? "" : ASTAnalyzer.getOriginalSourceFromRoot(node);
	}}; 
	
	private F<ISourceChange, int[]> getChangedLineInCompilationUnit = 
		new F<ISourceChange, int[]>() {
		@Override
		public int[] f(ISourceChange change) {
			final int[] lines = new int[]{0, 0, 0};
			String[] linesBefore = getSource.f(change.getNodeBefore()).split
				(System.lineSeparator());
			String[] linesAfter = getSource.f(change.getNodeAfter()).split
				(System.lineSeparator());
			Patch patch = DiffUtils.diff(Arrays.asList(linesBefore), Arrays.
				asList(linesAfter));
			List<Delta> deltas = FunctionalJavaUtil.createListFromCollection(patch.
				getDeltas());
			deltas.foreach(new Effect<Delta>() {
				@Override
				public void e(Delta delta) {
					int original = delta.getOriginal().getLines().size();
					int revised = delta.getRevised().getLines().size();
					switch(delta.getType())
					{
					case CHANGE:
						lines[0] += original;
						break;
					case DELETE:
						lines[1] += original;
						break;
					case INSERT:
						lines[2] += revised;
						break;
					default: logger.fatal("Unkown patch type.");
					}
			}});
			return lines;
		}
	};
	
	
	public void startCompute(ISourceChange change)
	{
		this.result = getChangedLines.f(change).foldLeft(new F2<int[], int[], int[]>(){
			@Override
			public int[] f(int[] base, int[] element) {
				for(int i = 0 ; i < base.length; i ++)
					base[i] += element[i];
				return base;
			}}, new int[]{0,0,0});
	}
	
	
	public int getChangedLines() {
		return this.result[0];
	}
	
	public int getRemovedLines() {
		return this.result[1];
	}
	
	public int getAddedLines() {
		return this.result[2];
	}
}
