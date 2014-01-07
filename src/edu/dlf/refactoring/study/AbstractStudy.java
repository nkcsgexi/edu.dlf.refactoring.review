package edu.dlf.refactoring.study;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.EclipseUtils;
import edu.dlf.refactoring.utils.WorkQueueItem;
import fj.Effect;
import fj.data.List;

public abstract class AbstractStudy extends WorkQueueItem {

	private static final Pattern integerPattern = Pattern.compile("[0-9]+");

	abstract protected void study();
	
	private final Logger logger;
	
	public AbstractStudy(String itemName) {
		super(itemName);
		this.logger = ServiceLocator.ResolveType(Logger.class);
	}

	@Override
	protected void internalRun() {
		logger.info("Study starts.");
		study();
		logger.info("Study ends.");
	}
	
	protected Integer getContainedInteger(String path) {
		Matcher matcher = integerPattern.matcher(path);
		if(matcher.find()) {
			String text = matcher.group(0);
			return Integer.parseInt(text);
		}
		return null;
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
