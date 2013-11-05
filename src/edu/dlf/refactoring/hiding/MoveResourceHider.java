package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring;

public class MoveResourceHider extends AbstractRefactoringHider{

	private final Logger logger;

	@Inject
	public MoveResourceHider(Logger logger) {
		this.logger = logger;
	}
		
	@Override
	public ASTNode f(IDetectedRefactoring refactoring, ASTNode root) {
		
		
		
		return root;
	}

}
