package edu.dlf.refactoring.ui;


public interface ICodeReviewInput {
	public enum InputType{
		JavaElement,
		ASTNode
	}
	
	Object getInputBefore();
	Object getInputAfter();
	InputType getInputType();
}
