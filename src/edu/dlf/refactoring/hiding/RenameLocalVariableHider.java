package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.refactorings.DetectedRenameLocalVariable;

public class RenameLocalVariableHider extends AbstractRenameHider {

	@Inject
	public RenameLocalVariableHider(Logger logger) {
		super(logger);
	}

	@Override
	protected NodeListDescriptor getBeforeNamesDescriptor() {
		return DetectedRenameLocalVariable.SimpleNamesBefore;
	}

	@Override
	protected NodeListDescriptor getAfterNamesDescriptor() {
		return DetectedRenameLocalVariable.SimpleNamesAfter;
	}

	@Override
	protected boolean isSimpleNameToUpdate(ASTNode name) {
		return true;
	}

}
