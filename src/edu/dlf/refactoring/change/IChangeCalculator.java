package edu.dlf.refactoring.change;

import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.design.JavaElementPair;

public interface IChangeCalculator
{
	@Subscribe
	Void CalculateSourceChange(JavaElementPair pair);
}

