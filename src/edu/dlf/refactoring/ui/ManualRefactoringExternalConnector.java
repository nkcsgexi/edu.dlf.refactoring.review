package edu.dlf.refactoring.ui;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator.ChangeCompAnnotation;
import edu.dlf.refactoring.design.ServiceLocator.RefactoringCheckerCompAnnotation;
import edu.dlf.refactoring.utils.WorkQueue;
import fj.Equal;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class ManualRefactoringExternalConnector implements 
	IManualRefactoringInterface {
	private final Logger logger;
	private final IFactorComponent changeComp;
	private final IFactorComponent checkComp;
	private final WorkQueue queue;

	@Inject
	public ManualRefactoringExternalConnector(Logger logger,
		WorkQueue queue,
		@ChangeCompAnnotation IFactorComponent changeComp,
		@RefactoringCheckerCompAnnotation IFactorComponent checkComp) {
		this.logger = logger;
		this.changeComp = changeComp;
		this.checkComp = checkComp;
		this.queue = queue;
	}
	
	final Equal<ASTNode> grouper = FJUtils.getReferenceEq((ASTNode)null).
		comap(ASTNode2ASTNodeUtils.getRootFunc);
	
	private final F<ASTNode, IManualRefactoringPosition> getNodePostion = 
		new F<ASTNode, IManualRefactoringPosition>() {
			@Override
			public IManualRefactoringPosition f(final ASTNode node) {
				return new IManualRefactoringPosition() {
					@Override
					public int getStartOffset() {
						return node.getStartPosition();
					}
					@Override
					public String getPath() {
						return null;
					}
					@Override
					public int getLength() {
						return node.getLength();
					}
	};}};
	
	
	
	private final F<ICheckingResult, IManualRefactoringInfo> convert2RefactoringInfo = 
		new F<ICheckingResult, IManualRefactoringInfo>() {
			@Override
			public IManualRefactoringInfo f(ICheckingResult result) {
				final List<ASTNode> nodesBefore = result.getDetectedRefactoring().
					getEffectedNodesBefore();
				final List<ASTNode> nodesAfter = result.getDetectedRefactoring().
					getEffectedNodesAfter();
				return new IManualRefactoringInfo() {
					@Override
					public Collection<IManualRefactoringPosition> getRightRefactoringPostion() {
						return nodesAfter.map(getNodePostion).toCollection();
					}
					@Override
					public Collection<IManualRefactoringPosition> getLeftRefactoringPostion() {
						return nodesBefore.map(getNodePostion).toCollection();
					}
				};
	}};
	
	
	@Override
	public void provideInput(final IProject before, final IProject after, 
		final IManualRefactoringCallback callBack) {
		JavaElementPair pair = new JavaElementPair(JavaCore.create(before), 
			JavaCore.create(after));
		final Buffer<ICheckingResult> buffer = Buffer.empty();
		this.checkComp.registerListener(new ICompListener() {
			@Override
			public void callBack(Object output) {
				ICheckingResult result = (ICheckingResult)output;
				if(result.IsBehaviorPreserving()) buffer.snoc(result);
				if(queue.getQueueLength() == 0) {
					List<ICheckingResult> list = buffer.toList();
					callBack.callBack(list.map(convert2RefactoringInfo).toCollection());
				}
		}});
		this.changeComp.listen(pair);
	}
}
