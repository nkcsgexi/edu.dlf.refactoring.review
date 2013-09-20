package edu.dlf.refactoring.ui;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.dlf.refactoring.utils.UIUtils;

public class BeforeChangeCodeView extends ViewPart {

	private Text text;
	
	public BeforeChangeCodeView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
         this.text = new Text(parent, 12);
         this.text.setEditable(false);
         this.text.setFont(new Font(UIUtils.getDisplay(), "Arial", 12, 
        		 SWT.COLOR_BLACK));
	}

	@Override
	public void setFocus() {
	
	}

}
