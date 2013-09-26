package edu.dlf.refactoring.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class UIUtils {

	public static final Color Blue = new Color(getDisplay(), 0, 0, 255);
	public static final Color Green = new Color(getDisplay(), 0, 255, 0);
	public static final Color Red = new Color(getDisplay(), 255, 0, 0);
	public static final Font CodeFont = new Font(getDisplay(), "Courier", 12, 
			SWT.BOLD);

	private UIUtils() throws Exception {
		throw new Exception("");
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	
	

}
