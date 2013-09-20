package edu.dlf.refactoring.utils;

import org.eclipse.swt.widgets.Display;

public class UIUtils {

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
