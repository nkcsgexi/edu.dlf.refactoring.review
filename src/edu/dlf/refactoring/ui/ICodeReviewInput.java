package edu.dlf.refactoring.ui;

import fj.P2;


public interface ICodeReviewInput {
	public enum InputType{
		JavaElement,
		ASTNode
	}
	P2<Object, Object> getInputPair();
	InputType getInputType();
}
