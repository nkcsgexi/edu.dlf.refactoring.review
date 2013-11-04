package edu.dlf.refactoring.ui;

public class CodeViewAfterHiding extends CodeView{

	public CodeViewAfterHiding() {
		super();
	}
	
	@Override
	public void callBack(Object output) {
		UpdateCodeInternal((((StyledTextUpdater[])output)[1]));
	}

	@Override
	public void setFocus() {
		
	}

}
