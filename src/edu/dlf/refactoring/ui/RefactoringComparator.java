package edu.dlf.refactoring.ui;


import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Image;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.utils.UIUtils;

public class RefactoringComparator implements ICompListener{
	
	private final Logger logger;
	
	@Inject
	public RefactoringComparator(Logger logger) {
		this.logger = logger;
	}
	
	private static class RefactoringCompareInput extends CompareEditorInput{
	
		private final String version2;
		private final String version1;
	
		public RefactoringCompareInput(Boolean hided, String version1, String version2) {
			super(createConfig(hided));
			this.version1 = version1;
			this.version2 = version2;
		}
	
		private static CompareConfiguration createConfig(Boolean hided) {
			CompareConfiguration config = new CompareConfiguration();
			config.setLeftLabel("Baseline");
			config.setRightLabel(hided ? "Changes after hiding refactorings" 
				: "Changes with refactorings");
			config.setLeftImage(UIUtils.createImage("1.png"));
			config.setRightImage(UIUtils.createImage("2.png"));
			return config;
		}

		@Override
		protected Object prepareInput(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			Differencer diff = new Differencer();
			return diff.findDifferences(false, new NullProgressMonitor(), null,
				null, new Input(version1), new Input(version2));
		}
	
		private class Input implements ITypedElement, IStreamContentAccessor {
	
	        private final String fContent;
	
	        public Input(String s) {
	        	fContent = s;
	        }
	
	        public String getName() {
	            return "name";
	        }
	
	        public Image getImage() {
	            return null;
	        }
	
	        public String getType() {
	        	return ".java";
	        }
	
	        public InputStream getContents() throws CoreException {
	           return IOUtils.toInputStream(fContent);
	        }
		}
	}

	@Override
	public void callBack(Object output) {
		final String before = ((StyledTextUpdater[])output)[0].getText();
		final String middle = ((StyledTextUpdater[])output)[1].getText();
		final String after = ((StyledTextUpdater[])output)[2].getText();
		UIUtils.RunInUIThread(new Runnable() {
			@Override
			public void run() {
				CompareUI.openCompareEditor(new RefactoringCompareInput(false, before, 
					after));
		}});
		
		UIUtils.RunInUIThread(new Runnable() {
			@Override
			public void run() {
				CompareUI.openCompareEditor(new RefactoringCompareInput(true, before, 
					middle));
		}});
	}
}


