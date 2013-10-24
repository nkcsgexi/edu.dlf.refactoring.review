package edu.dlf.refactoring.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

public class UIUtils {

	public static final Color Blue = new Color(getDisplay(), 0, 0, 255);
	public static final Color Green = new Color(getDisplay(), 0, 255, 0);
	public static final Color Red = new Color(getDisplay(), 255, 0, 0);
	public static final Color Black = new Color(getDisplay(), 0, 0, 0);
	public static final Font CodeFont = new Font(getDisplay(), "Courier", 12, 
			SWT.BOLD);
	
	private static int currentColor = 0;
	private static final Color[] allColors = new Color[] {
		new Color(getDisplay(), 0, 0, 255),      //Blue
	    new Color(getDisplay(), 255, 0, 0),      //Red
	    new Color(getDisplay(), 0, 255, 0),      //Green
	 //   new Color(getDisplay(), 255, 255, 0),    //Yellow
	    new Color(getDisplay(), 255, 0, 255),    //Magenta
	    new Color(getDisplay(), 255, 128, 128),  //Pink
	    new Color(getDisplay(), 128, 128, 128),  //Gray
	    new Color(getDisplay(), 128, 0, 0),      //Brown
	    new Color(getDisplay(), 255, 128, 0),    //Orange
	};
	

	private UIUtils() throws Exception {
		throw new Exception("");
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
	
	public static Color getNextColor() {
		return allColors[currentColor++ % allColors.length];
	}
	
	

}
