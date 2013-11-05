package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.refactorings.DetectedRenameMethodRefactoring;

public class RenameMethodHider extends AbstractRenameHider{

	@Inject
	public RenameMethodHider(Logger logger) {
		super(logger);
	}

	@Override
	protected NodeListDescriptor getBeforeNamesDescriptor() {
		return DetectedRenameMethodRefactoring.SimpleNamesBefore;
	}

	@Override
	protected NodeListDescriptor getAfterNamesDescriptor() {
		return DetectedRenameMethodRefactoring.SimpleNamesAfter;
	}

	@Override
	protected boolean isSimpleNameToUpdate(ASTNode name) {
		return true;
	}

}
