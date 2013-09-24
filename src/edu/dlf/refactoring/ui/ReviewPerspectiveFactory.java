package edu.dlf.refactoring.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ReviewPerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
	
		layout.addStandaloneView("RefReview.DiffTreeView", true, 
				IPageLayout.LEFT, 0.2f, layout.getEditorArea());
		layout.addStandaloneView("RefReview.BeforeChangeCodeView", true, 
				IPageLayout.LEFT, 0.4f, layout.getEditorArea());
		layout.addStandaloneView("RefReview.AfterChangeCodeView", true, 
				IPageLayout.LEFT, 0.4f, layout.getEditorArea());
		
	}
}
