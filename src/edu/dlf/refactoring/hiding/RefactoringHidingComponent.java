package edu.dlf.refactoring.hiding;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.MoveResource;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameField;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameLocalVariable;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import edu.dlf.refactoring.utils.WorkQueueItem;
import fj.F;

public class RefactoringHidingComponent implements IFactorComponent{

	private final ExecutorService queue;
	private final Logger logger;
	private final HashMap<RefactoringType, AbstractRefactoringHider> allHiders;
	
	private final F<ASTNode, F<IDetectedRefactoring, ASTNode>> folder = 
		new F<ASTNode, F<IDetectedRefactoring,ASTNode>>() {
		@Override
		public F<IDetectedRefactoring, ASTNode> f(final ASTNode root) {
			return new F<IDetectedRefactoring, ASTNode>() {
				@Override
				public ASTNode f(IDetectedRefactoring refactoring) {
					return allHiders.containsKey(refactoring.getRefactoringType()) 
						? allHiders.get(refactoring.getRefactoringType()).
							f(refactoring, root) : root;
		}};}};
	
	
	@Inject
	public RefactoringHidingComponent(Logger logger, ExecutorService queue,
			@ExtractMethod AbstractRefactoringHider emHider,
			@RenameMethod AbstractRefactoringHider rmHider,
			@RenameType AbstractRefactoringHider rtHider,
			@RenameLocalVariable AbstractRefactoringHider rlvHider,
			@RenameField AbstractRefactoringHider rfHider,
			@MoveResource AbstractRefactoringHider mrHider)
	{
		this.logger = logger;
		this.queue = queue;
		this.allHiders = new HashMap<RefactoringType, AbstractRefactoringHider>();
		allHiders.put(RefactoringType.ExtractMethod, emHider);
		allHiders.put(RefactoringType.RenameMethod, rmHider);
		allHiders.put(RefactoringType.RenameType, rtHider);
		allHiders.put(RefactoringType.RenameLocalVariable, rlvHider);
		allHiders.put(RefactoringType.RenameField, rfHider);
		allHiders.put(RefactoringType.Move, mrHider);
	}
	
	
	@Override
	public Void listen(final Object event) {
		if(event instanceof IHidingComponentInput) {
			queue.execute(new WorkQueueItem("Hide"){
			@Override
			public void internalRun() {
				IHidingComponentInput input = (IHidingComponentInput) event;
				input.callback(input.getHideRefactorings().foldLeft(folder, 
					input.getRootNode()));				
			}});}
		return null;
	}

	@Override
	public Void registerListener(ICompListener listener) {
		return null;
	}

}
