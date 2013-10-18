package edu.dlf.refactoring.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import edu.dlf.refactoring.design.ComponentsRepository;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.UIUtils;

public abstract class CodeView extends ViewPart implements ICompListener {

	private StyledText styledText;
	private final IFactorComponent component;
	
	protected CodeView() {
		super();
		this.component = ((ComponentsRepository)ServiceLocator.ResolveType
			(ComponentsRepository.class)).getUIComponent();
	}
	
	@Override
	public final void createPartControl(Composite parent) {    
		this.styledText = new StyledText(parent, SWT.BORDER|SWT.SCROLL_LINE|
			SWT.V_SCROLL);
		this.styledText.setEditable(false);
		this.styledText.setBackground(new Color(UIUtils.getDisplay(), 
				255,255,255));
		this.component.registerListener(this);
	}
	
	protected void UpdateCodeInternal(StyledTextUpdater updater)
	{
		updater.UpdateStyledText(styledText);
	}
}
