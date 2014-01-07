package edu.dlf.refactoring.study;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;

import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.DlfFileUtils;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.ServiceLocator.ChangeCompAnnotation;
import edu.dlf.refactoring.utils.EclipseUtils;
import fj.Effect;
import fj.F;
import fj.F2;
import fj.P;
import fj.P2;
import fj.data.List;

public class ImportSubjects extends AbstractStudy {

	private final Logger logger;
	private final String root;
	private final IFactorComponent changeComp;
	
	private static final int index = 0;
	private static final String[] projectNames = {"junit"};
	private static final String[] studyFolders = {"junit-study/"};
	
	private static final Pattern projectNamePattern = Pattern.
		compile(projectNames[index] + "[0-9]+");
	
	@Inject
	public ImportSubjects(
		Logger logger,
		@ChangeCompAnnotation IFactorComponent changeComp) {
		super(projectNames[index] + " study");
		this.logger = logger;
		this.root = DlfFileUtils.desktop + studyFolders[index];
		this.changeComp = changeComp;
	}

	private final F2<String, Integer, List<IProject>> importAllProjects = 
		new F2<String, Integer, List<IProject>>() {
		@Override
		public List<IProject> f(final String path, final Integer count) {
			List<File> files = DlfFileUtils.searchFilesInDirectory.f(path, 
				new F<File, Boolean>() {
				@Override
				public Boolean f(File file) {
					return file.getName().equals(".project") && !file.
						getAbsolutePath().contains("bin");
			}});
			List<IProject> importedProjects = files.map(DlfFileUtils.getPath.
				andThen(EclipseUtils.importGetProject));	
			importedProjects.zip(importedProjects.map(EclipseUtils.getProjectName).
				map(new F<String, String>(){
				@Override
				public String f(String oldName) {
					return oldName + count;
			}})).foreach(EclipseUtils.directlyRenameProject.tuple());
			return importedProjects;
	}}; 
	
	private final F<String, P2<String, Integer>> appendProjectNumber = 
		new F<String, P2<String,Integer>>() {
		@Override
		public P2<String, Integer> f(String path) {
			return P.p(path, getContainedInteger(path));
	}};
	
	@Override
	protected void study() {
		List<P2<String, Integer>> folders = DlfFileUtils.getSubDirectories.f(root).filter
			(new F<String, Boolean>(){
			@Override
			public Boolean f(String path) {
				return getContainedInteger(path) != null;
		}}).map(appendProjectNumber);
		folders.foreach(new Effect<P2<String, Integer>>() {
			@Override
			public void e(P2<String, Integer> p) {
				logger.info(p._1() + ":" + p._2());
		}});
		folders.bind(importAllProjects.tuple());
	}
}
