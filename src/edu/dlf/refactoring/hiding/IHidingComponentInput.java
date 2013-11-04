package edu.dlf.refactoring.hiding;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.dlf.refactoring.design.IDetectedRefactoring;
import fj.data.List;

public interface IHidingComponentInput{
	List<IDetectedRefactoring> getHideRefactorings();
	ASTNode getRootNode();
	void callback(ASTNode rootAfterHiding);
}
