package edu.dlf.refactoring.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ComponentsRepository;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.ui.ICodeReviewInput.InputType;
import edu.dlf.refactoring.utils.EclipseUtils;
import edu.dlf.refactoring.utils.WorkQueue;

public class RefReviewCommandHandler extends AbstractHandler {

	private final WorkQueue queue = ServiceLocator.ResolveType(WorkQueue.class);
	private final IFactorComponent changeComp = ((ComponentsRepository)
		ServiceLocator.ResolveType(ComponentsRepository.class)).
			getChangeComponent();
	private final CodeReviewUIComponent context = (CodeReviewUIComponent) 
		((ComponentsRepository)ServiceLocator.ResolveType(
			ComponentsRepository.class)).getUIComponent();
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
				if(input.getInputType() == InputType.ASTNode) {
					changeComp.listen(new ASTNodePair((ASTNode) input.getInputBefore(),
						(ASTNode) input.getInputAfter()));
				}
				else {
					JavaElementPair pair = new JavaElementPair((IJavaElement)
						input.getInputBefore(), (IJavaElement)input.
							getInputAfter());
					changeComp.listen(pair);
			}}});}
		
		if(id.equals("RefReview.import")){
			queue.execute(new Runnable(){
				@Override
				public void run() {
					String path = "/home/xige/Desktop/pre-checking3";
					EclipseUtils.importProject.e(path);
					String name = EclipseUtils.getProjectName.f(path);
					EclipseUtils.renameProject.f(name, "newname");
			}});
		}
		
		return null;
	}
}
