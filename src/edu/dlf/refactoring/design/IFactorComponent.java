package edu.dlf.refactoring.design;

import com.google.common.eventbus.Subscribe;

public interface IFactorComponent {
	
	@Subscribe
	Void listen(Object event);
}
