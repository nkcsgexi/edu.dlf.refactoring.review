package edu.dlf.refactoring.change;

import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.design.ISourceChange;
import edu.dlf.refactoring.design.JavaElementPair;

public interface IJavaModelChangeCalculator {
	@Subscribe
	public abstract ISourceChange calculate(JavaElementPair pair);
}

