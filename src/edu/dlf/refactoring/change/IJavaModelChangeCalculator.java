package edu.dlf.refactoring.change;

import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.design.JavaElementPair;

public interface IJavaModelChangeCalculator
{
	@Subscribe
	Void CalculateSourceChange(JavaElementPair pair);
}

