package edu.dlf.refactoring.study;

import com.google.inject.Inject;

public class ClearWorkSpace extends AbstractStudy {

	@Inject
	public ClearWorkSpace() {
		super("Clean work space");
	}

	@Override
	protected void study() {
		clearWorkspace();
	}
}
