package edu.dlf.refactoring.ui;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTNode2ASTNodeUtils;
import edu.dlf.refactoring.analyzers.ASTNode2StringUtils;
import edu.dlf.refactoring.analyzers.FJUtils;
import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.RefactoringType;
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
	public ManualRefactoringExternalConnector(
		Logger logger,
		WorkQueue queue,
		@ChangeCompAnnotation IFactorComponent changeComp,
		@RefactoringCheckerCompAnnotation IFactorComponent checkComp) {
			this.logger = logger;
			this.changeComp = changeComp;
			this.checkComp = checkComp;
			this.queue = queue;
	}
	
	private final Equal<ASTNode> grouper = FJUtils.getReferenceEq((ASTNode)null).
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
						return ASTNode2StringUtils.getFilePath.f(node);
					}
					@Override
					public int getLength() {
						return node.getLength();
					}
					
					@Override
					public String toString() {
						return getPath() + ": " + getStartOffset() + " " + 
							getLength();
					}
	};}};
	
	private final F<ICheckingResult, IManualRefactoringInfo> convert2RefactoringInfo = 
		new F<ICheckingResult, IManualRefactoringInfo>() {
			@Override
			public IManualRefactoringInfo f(final ICheckingResult result) {
				final List<IManualRefactoringPosition> positionsBefore = result.
					getDetectedRefactoring().getEffectedNodesBefore().map
						(getNodePostion);
				final List<IManualRefactoringPosition> positionsAfter = result.
					getDetectedRefactoring().getEffectedNodesAfter().map
						(getNodePostion);
				return new IManualRefactoringInfo() {
					@Override
					public String toString() {
						StringBuilder sb = new StringBuilder();
						sb.append(getRefactoringType() + System.lineSeparator());
						sb.append("Left files:" + System.lineSeparator());
						for(IManualRefactoringPosition postion : getLeftRefactoringPostions())
						{
							sb.append(postion.getPath() + System.lineSeparator());
						}
						sb.append("Right files:" + System.lineSeparator());
						for(IManualRefactoringPosition postion : getRightRefactoringPostions())
						{
							sb.append(postion.getPath() + System.lineSeparator());
						}
						return sb.toString();
					}
					@Override
					public Collection<IManualRefactoringPosition> 
						getRightRefactoringPostions() {
							return positionsAfter.toCollection();
					}
					@Override
					public Collection<IManualRefactoringPosition> 
						getLeftRefactoringPostions() {
							return positionsBefore.toCollection();
					}
					@Override
					public RefactoringType getRefactoringType() {
						return result.getDetectedRefactoring().getRefactoringType();
					}};
	}};
	
	
	@Override
	public void startRefactoringDetection(final IProject before, final IProject after, 
		final IManualRefactoringCallback callBack) {
		JavaElementPair pair = new JavaElementPair(JavaCore.create(before), 
			JavaCore.create(after));
		final Buffer<ICheckingResult> buffer = Buffer.empty();
		this.checkComp.registerListener(new ICompListener() {
			@Override
			public void callBack(final Object output) {
				new Thread(){
					@Override
					public void run() {
						ICheckingResult result = (ICheckingResult)output;
						if(result.IsBehaviorPreserving()) buffer.snoc(result);
						if(queue.getQueueLength() == 0) {
							List<ICheckingResult> list = buffer.toList();
							callBack.callBack(list.map(convert2RefactoringInfo).
								toCollection());
				}}}.start();
		}});
		this.changeComp.listen(pair);
	}
}
