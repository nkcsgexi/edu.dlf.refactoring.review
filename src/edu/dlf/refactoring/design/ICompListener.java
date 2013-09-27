package edu.dlf.refactoring.design;

import com.google.common.eventbus.Subscribe;

public interface ICompListener {
	
	@Subscribe
	void callBack(Object output);
}
