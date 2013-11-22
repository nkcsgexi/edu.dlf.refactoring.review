package edu.dlf.refactoring.hiding;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.IDetectedRefactoring.NodeListDescriptor;
import edu.dlf.refactoring.refactorings.DetectedRenameField;

public class RenameFieldHider extends AbstractRenameHider{

	@Inject
	public RenameFieldHider(Logger logger) {
		super(logger);
	}

	@Override
	protected NodeListDescriptor getBeforeNamesDescriptor() {
		return DetectedRenameField.SimpleNamesBefore;
	}

	@Override
	protected NodeListDescriptor getAfterNamesDescriptor() {
		return DetectedRenameField.SimpleNamesAfter;
	}

	@Override
	protected boolean isSimpleNameToUpdate(ASTNode name) {
		return true;
	}

}
