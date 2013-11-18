package edu.dlf.refactoring.utils;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;

import edu.dlf.refactoring.analyzers.JavaModelAnalyzer;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Hash;
import fj.Unit;
import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;

public class EclipseUtils {
	
	private static final HashMap<String, IProject> allImportedProjects = 
		new HashMap<String, IProject>(Equal.stringEqual, Hash.stringHash);
	private static final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private EclipseUtils() throws Exception
	{
		throw new Exception();
	}
	
	public static final IPath workSpacePath = ResourcesPlugin.getWorkspace().getRoot().
		getLocation();
	

	public static final List<IProject> getAllImportedProjects() {
		return allImportedProjects.values();
	}
	
	public static final Effect<String> importProject = new Effect<String>() {
		@Override
		public void e(String projectPath) {
			try {
			IProjectDescription description = ResourcesPlugin.getWorkspace().
				loadProjectDescription(new Path(projectPath + "/.project"));
			IProject project = ResourcesPlugin.getWorkspace().getRoot().
				getProject(description.getName());
			project.create(description, new NullProgressMonitor()); 
			project.open(new NullProgressMonitor());
			allImportedProjects.set(projectPath, project);
			} catch (Exception e) {
				logger.fatal("Import error: " + e);
	}}};
	
	public static final F<String, String> getProjectNameByPath = new F<String, String>() {
		@Override
		public String f(final String path) {
			Option<IProject> projectFinder = allImportedProjects.get(path);
			if(projectFinder.isNone()) {
				logger.fatal("Cannot find project at: " + path);
				return "";
			}
			return projectFinder.some().getName();
	}}; 
	
	
	public static final Effect<String> removeProject = new Effect<String>() {
		@Override
		public void e(final String projectName) {
			allImportedProjects.values().foreach(new Effect<IProject>() {
				@Override
				public void e(IProject project) {
					try {
						if(project.getName().equals(projectName))
							project.delete(false, true, new NullProgressMonitor());
					} catch (Exception e) {
						logger.fatal(e);
	}}});}};
	
	public static final F<String, Option<IJavaElement>> findJavaProjecInWorkspacetByName =
		new F<String, Option<IJavaElement>>() {
			@Override
			public Option<IJavaElement> f(final String name) {
				return JavaModelAnalyzer.getJavaProjectsInWorkSpace().find(
					new F<IJavaElement, Boolean>() {
					@Override
					public Boolean f(IJavaElement element) {
						return element.getElementName().equals(name);
	}});}};
		
	public static final F2<String, String, Unit> renameProject = 
		new F2<String, String, Unit>() {
		@Override
		public Unit f(final String oldName, final String newName) {
			try{
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
			IProjectDescription descripor = project.getDescription();
			descripor.setName(newName);
			project.move(descripor, true, new NullProgressMonitor());
			} catch (Exception e) {
				logger.fatal("Rename project error " + e);
			}
			return Unit.unit();
	}};
}
