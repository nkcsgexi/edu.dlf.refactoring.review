package edu.dlf.refactoring.ui;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.utils.FileUtil;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.FileUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ComponentsRepository;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.ui.ICodeReviewInput.InputType;
import edu.dlf.refactoring.utils.EclipseUtils;
import edu.dlf.refactoring.utils.WorkQueue;
import fj.Effect;
import fj.F;
import fj.P2;
import fj.data.List;

public class RefReviewCommandHandler extends AbstractHandler {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final WorkQueue queue = ServiceLocator.ResolveType(WorkQueue.class);
	private final IFactorComponent changeComp = ((ComponentsRepository)
		ServiceLocator.ResolveType(ComponentsRepository.class)).
			getChangeComponent();
	private final CodeReviewUIComponent context = (CodeReviewUIComponent) 
		((ComponentsRepository)ServiceLocator.ResolveType(
			ComponentsRepository.class)).getUIComponent();
	private final ICodeReviewInput input = ServiceLocator.ResolveType
			(ICodeReviewInput.class);
	
	private final Effect<P2<String, Integer>> importProject = new Effect<P2<String, 
		Integer>>() {
		@Override
		public void e(P2<String, Integer> pair) {
			EclipseUtils.importProject.e(pair._1());
			String name = EclipseUtils.getProjectNameByPath.f(pair._1());
			EclipseUtils.renameProject.f(name, name + pair._2());
	}}; 

	
	
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
					List<String> directories = FileUtils.getSubDirectories.
						f(FileUtils.desktop).filter(new F<String, Boolean>() {
							@Override
							public Boolean f(String path) {
								return path.contains("pre-checking");
					}});
					directories.zipIndex().foreach(importProject);
					
			}});
		}
		return null;
	}
}
