package edu.dlf.refactoring.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.design.IASTNodePair;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.ui.ICodeReviewInput.InputType;
import edu.dlf.refactoring.utils.WorkQueue;

public class RefReviewCommandHandler extends AbstractHandler {

	private final WorkQueue queue = ServiceLocator.ResolveType(WorkQueue.class);
	private final IFactorComponent changeComp = ServiceLocator.ResolveType
			(ChangeComponent.class);
	private final CodeReviewUIComponent context = ServiceLocator.ResolveType
			(CodeReviewUIComponent.class); 
	private final ICodeReviewInput input = ServiceLocator.ResolveType
			(ICodeReviewInput.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getCommand().getId();
		if(id.equals("RefReview.CalculateDiff")){
			queue.execute(new Runnable(){
				@Override
				public void run() {
					context.clearContext();
					if(input.getInputType() == InputType.ASTNode)
					{
						changeComp.listen(new IASTNodePair() {
							@Override
							public ASTNode getNodeBefore() {
								return (ASTNode) input.getInputBefore();
							}
							@Override
							public ASTNode getNodeAfter() {
								return (ASTNode) input.getInputAfter();
							}
						});
					}
					else{
						JavaElementPair pair = new JavaElementPair((IJavaElement)
							input.getInputBefore(), (IJavaElement)input.
								getInputAfter());
						changeComp.listen(pair);
					}
				}});
		}
		return null;
	}
}
