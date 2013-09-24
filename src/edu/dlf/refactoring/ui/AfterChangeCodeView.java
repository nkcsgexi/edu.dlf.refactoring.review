package edu.dlf.refactoring.ui;


public class AfterChangeCodeView extends CodeView {

	public AfterChangeCodeView() {
		super();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void updateCodeViews(StyledTextUpdater[] updaters) {
		this.UpdateCodeInternal(updaters[1]);
	}
}
