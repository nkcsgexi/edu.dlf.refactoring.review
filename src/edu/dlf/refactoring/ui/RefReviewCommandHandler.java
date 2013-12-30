package edu.dlf.refactoring.ui;

import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.analyzers.DlfFileUtils;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ComponentsRepository;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.study.ClearWorkSpace;
import edu.dlf.refactoring.study.CompareProjectsInWorkspaceStudy;
import edu.dlf.refactoring.study.ImportSubjects;
import edu.dlf.refactoring.ui.ICodeReviewInput.InputType;
import edu.dlf.refactoring.utils.EclipseUtils;
import edu.dlf.refactoring.utils.WorkQueueItem;
import fj.Effect;
import fj.F;
import fj.P2;
import fj.data.List;

public class RefReviewCommandHandler extends AbstractHandler {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final ExecutorService queue = ServiceLocator.ResolveType(ExecutorService.class);
	private final IFactorComponent changeComp = ((ComponentsRepository)
		ServiceLocator.ResolveType(ComponentsRepository.class)).
			getChangeComponent();
	private final CodeReviewUIComponent context = (CodeReviewUIComponent) 
		((ComponentsRepository)ServiceLocator.ResolveType(
			ComponentsRepository.class)).getUIComponent();
	private final ICodeReviewInput input = ServiceLocator.ResolveType
			(ICodeReviewInput.class);
	
	private final Effect<String> importProject = new Effect<String>() {
		@Override
		public void e(String path) {
			try {
			EclipseUtils.importProject.e(path);
			String name = EclipseUtils.getProjectNameByPath.f(path);
			EclipseUtils.renameProject.f(name, name + DlfFileUtils.generateRandomInteger.
				f(1000).toString());
			}catch(Exception e) {
				logger.fatal(e);
	}}}; 
	
	private final WorkQueueItem importWorkItem = new WorkQueueItem("ImportButton"){
		@Override
		public void internalRun() {
			List<String> directories = DlfFileUtils.getSubDirectories.
				f(DlfFileUtils.desktop).filter(new F<String, Boolean>() {
					@Override
					public Boolean f(String path) {
						return path.contains("checking");
			}});
			directories.foreach(importProject);
	}};
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getCommand().getId();
		if(id.equals("RefReview.CalculateDiff")){
			queue.execute(new WorkQueueItem("DiffButton"){
			@Override
			public void internalRun() {
				context.clearContext();
				P2<Object, Object> inputPair = input.getInputPair();
				if(input.getInputType() == InputType.ASTNode) {
					changeComp.listen(new ASTNodePair((ASTNode) inputPair._1(),
						(ASTNode) inputPair._2()));
				}
				else {
					JavaElementPair pair = new JavaElementPair((IJavaElement)
						inputPair._1(), (IJavaElement)inputPair._2());
					changeComp.listen(pair);
		}}});}
		
		if(id.equals("RefReview.import")) {
			queue.execute((WorkQueueItem)(ServiceLocator.ResolveType(
				ImportSubjects.class)));	
		}
		
		if(id.equals("RefReview.startStudy")) {
			queue.execute((WorkQueueItem)(ServiceLocator.ResolveType(
				CompareProjectsInWorkspaceStudy.class)));	
		}
		
		if(id.equals("RefReview.clearWorkspace")) {
			queue.execute((WorkQueueItem)(ServiceLocator.ResolveType(
				ClearWorkSpace.class)));	
		}
		
	
		return null;
	}
}
