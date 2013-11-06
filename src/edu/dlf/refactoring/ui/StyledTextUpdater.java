package edu.dlf.refactoring.ui;


import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.data.List;

public class StyledTextUpdater {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private List<StyleRange> styles = List.nil();
	private String text;
	
	public void addStyle(Color color, Font font, int start, int length)
	{
		StyleRange style = new StyleRange();
		style.start = start;
		style.length = length;
		style.foreground = color;
		style.font = font;
		style.fontStyle = SWT.NORMAL;
		styles = styles.snoc(style);
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void UpdateStyledText(final StyledText st)
	{
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
				st.setText(text);
				styles.foreach(new Effect<StyleRange>(){
					@Override
					public void e(StyleRange range) {
						st.setStyleRange(range);
					}});
		    }
		});
	}
	
}
