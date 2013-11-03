package edu.dlf.refactoring.ui;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.checkers.ICheckingResult;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import fj.data.List;

public class ASTWriter {

	private final Logger logger;

	@Inject
	public ASTWriter(Logger logger)
	{
		this.logger = logger;
	}
	
	public ASTNode markCorrectRefactoring(List<ICheckingResult> results)
	{
		
		
		return null;
	}
	
	
	
	
}
