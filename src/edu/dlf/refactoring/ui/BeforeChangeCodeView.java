package edu.dlf.refactoring.ui;



public class BeforeChangeCodeView extends CodeView {
	
	public BeforeChangeCodeView() {
		super();
	}

	@Override
	public void setFocus() {
	
	}

	@Override
	public void updateCodeViews(StyledTextUpdater[] updaters) {
		UpdateCodeInternal(updaters[0]);
	}
}
