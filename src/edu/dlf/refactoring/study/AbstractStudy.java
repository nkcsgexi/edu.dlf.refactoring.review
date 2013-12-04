package edu.dlf.refactoring.study;

import org.eclipse.core.resources.IProject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.utils.EclipseUtils;
import edu.dlf.refactoring.utils.WorkQueueItem;
import fj.Effect;
import fj.data.List;

public abstract class AbstractStudy extends WorkQueueItem {

	abstract protected void study();
	
	public AbstractStudy(String itemName) {
		super(itemName);
	}

	@Override
	protected void internalRun() {
		study();
	}

	protected void clearWorkspace() {
		List<IProject> projects = JavaModelAnalyzer.getAllProjectsInWorkSpace();
		projects.foreach(new Effect<IProject>() {
			@Override
			public void e(IProject project) {
				String newName = project.getName().replaceAll("\\d*$", "");
				EclipseUtils.directlyRenameProject.f(project, newName);
				EclipseUtils.directlyRemoveProject.e(project);
		}});
	}
	
}
