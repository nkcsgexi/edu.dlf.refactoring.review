package edu.dlf.refactoring.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import fj.Effect;
import fj.data.List.Buffer;

public class StyledTextUpdater {

	private final StringBuilder sb = new StringBuilder();
	private final Buffer<StyleRange> styles = Buffer.empty();
	
	public void addText(String text, Color color, Font font)
	{
		StyleRange style = new StyleRange();
		style.start = sb.length();
		style.length = text.length();
		style.foreground = color;
		style.font = font;
		style.fontStyle = SWT.NORMAL;
		styles.snoc(style);
		sb.append(text);
	}
	
	public void UpdateStyledText(final StyledText st)
	{
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
				st.setText(sb.toString());
				styles.toList().foreach(new Effect<StyleRange>(){
					@Override
					public void e(StyleRange range) {
						st.setStyleRange(range);
					}});
		    }
		});
	}
	
}
