package edu.dlf.refactoring.ui;

import java.util.Collection;

public interface IManualRefactoringCallback {
	void callBack(Collection<IManualRefactoringInfo> results);
}
