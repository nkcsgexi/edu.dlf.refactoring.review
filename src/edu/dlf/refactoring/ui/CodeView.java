package edu.dlf.refactoring.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.utils.UIUtils;

public abstract class CodeView extends ViewPart {

	private StyledText styledText;
	
	protected CodeView() {
		super();
	}
	
	@Override
	public final void createPartControl(Composite parent) {    
		this.styledText = new StyledText(parent, SWT.BORDER | SWT.SCROLL_LINE);
		this.styledText.setEditable(false);
		this.styledText.setBackground(new Color(UIUtils.getDisplay(),
				255,255,255));
		
		StyledTextUpdater updator = new StyledTextUpdater();
		updator.addText("This is first line\n", UIUtils.Green, UIUtils.CodeFont);
		updator.addText("This is second line\n", UIUtils.Blue, UIUtils.CodeFont);
		updator.addText("This is third line\n", UIUtils.Red, UIUtils.CodeFont);
		updator.UpdateStyledText(this.styledText);
	}
	
	protected void UpdateCodeInternal(StyledTextUpdater updater)
	{
		updater.UpdateStyledText(styledText);
	}
	
	@Subscribe
	public abstract void updateCodeViews(StyledTextUpdater[] updaters); 

}
