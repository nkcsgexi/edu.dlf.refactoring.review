package edu.dlf.refactoring.utils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.F;
import fj.F2;
import fj.Unit;
import fj.data.List;
import fj.data.Option;

public class EclipseUtils {
	
	private static List<IProject> allImportedProjects = List.nil();
	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private EclipseUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static final IPath workSpacePath = ResourcesPlugin.getWorkspace().getRoot().
		getLocation();
	

	public static List<IProject> getAllImportedProjects() {
		return allImportedProjects;
	}
	
	public static Effect<String> importProject = new Effect<String>() {
		@Override
		public void e(String projectPath) {
			try {
			IProjectDescription description = ResourcesPlugin.getWorkspace().
				loadProjectDescription(new Path(projectPath + "/.project"));
			IProject project = ResourcesPlugin.getWorkspace().getRoot().
				getProject(description.getName());
			project.create(description, null); 
			project.open(new NullProgressMonitor());
			allImportedProjects = allImportedProjects.snoc(project);
			} catch (Exception e) {
				logger.fatal(e);
			}
	}};
	
	public static F<String, String> getProjectNameByPath = new F<String, String>() {
		@Override
		public String f(final String path) {
			Option<IProject> projectFinder = getAllImportedProjects().find(
				new F<IProject, Boolean>() {
				@Override
				public Boolean f(IProject project) {
					return project.getLocation().toOSString().equals(new Path(path).
						toOSString());
			}});
			if(projectFinder.isNone()) {
				logger.fatal("Cannot find project at: " + path);
				return "";
			}
			return projectFinder.some().getName();
	}}; 
	
	
	public static Effect<String> removeProject = new Effect<String>() {
		@Override
		public void e(final String projectName) {
			allImportedProjects.foreach(new Effect<IProject>() {
				@Override
				public void e(IProject project) {
					try {
						if(project.getName().equals(projectName))
							project.delete(false, true, new NullProgressMonitor());
					} catch (Exception e) {
						logger.fatal(e);
	}}});}};
	
	
	public static F2<String, String, Unit> renameProject = 
		new F2<String, String, Unit>() {
		@Override
		public Unit f(final String oldName, final String newName) {
			Option<IProject> projectFinder = getAllImportedProjects().find
				(new F<IProject, Boolean>() {
				@Override
				public Boolean f(IProject project) {
					return project.getName().equals(oldName);
				}});
			if(projectFinder.isNone()) {
				logger.fatal("Cannot find project: " + oldName);
				return Unit.unit();
			}
			IProject project = projectFinder.some();
			try {
				IProjectDescription descripor = project.getDescription();
				descripor.setName(newName);
				project.move(descripor, true, new NullProgressMonitor());
			} catch (Exception e) {
				logger.fatal(e);
			}
			return Unit.unit();
	}};
}
