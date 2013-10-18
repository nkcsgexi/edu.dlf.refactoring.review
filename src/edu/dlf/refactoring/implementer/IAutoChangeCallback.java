package edu.dlf.refactoring.implementer;

import edu.dlf.refactoring.design.ISourceChange;
import fj.data.List;

public interface IAutoChangeCallback {
	void onFinishChanges(List<ISourceChange> cuChanges);
}
