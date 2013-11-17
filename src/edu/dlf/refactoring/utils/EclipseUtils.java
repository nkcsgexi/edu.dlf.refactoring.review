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
import fj.data.List;

public class EclipseUtils {
	
	private static List<IProject> allImportedProjects = List.nil();
	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private EclipseUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static IPath workSpacePath = ResourcesPlugin.getWorkspace().getRoot().
		getLocation();
	

	public List<IProject> getAllImportedProjects() {
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
	
	public static Effect<String> removeProject = new Effect<String>() {
		@Override
		public void e(final String projectName) {
			allImportedProjects.foreach(new Effect<IProject>() {
				@Override
				public void e(IProject project) {
					try {
						if(project.getName().equals(projectName))
							project.delete(false, true, new NullProgressMonitor());
					} catch (CoreException e) {
						logger.fatal(e);
	}}});}};
}
