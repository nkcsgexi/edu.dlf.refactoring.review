package edu.dlf.refactoring.ui;


public class AfterChangeCodeView extends CodeView {

	public AfterChangeCodeView() {
		super();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void callBack(Object output) {
		UpdateCodeInternal(((StyledTextUpdater[])output)[2]);
	}
}
