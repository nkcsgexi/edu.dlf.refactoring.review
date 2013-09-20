package edu.dlf.refactoring.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class AfterChangeCodeView extends ViewPart {

	private Label label;

	public AfterChangeCodeView() {
		
	
	}

	@Override
	public void createPartControl(Composite parent) {
		label = new Label(parent, 0);
        label.setText("After");

	}

	@Override
	public void setFocus() {

	}

}
