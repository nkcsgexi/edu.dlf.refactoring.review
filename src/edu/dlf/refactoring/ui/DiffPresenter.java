package edu.dlf.refactoring.ui;

import com.google.common.eventbus.Subscribe;

import edu.dlf.refactoring.checkers.ICheckingResult;

public class DiffPresenter {
	
	@Subscribe
	public void handleResult(ICheckingResult result)
	{
		
	}
	
	@Subscribe
	public void sourceCodeChange(String codeBefore, String codeAfter)
	{
		
	}
	
	
	public StyledTextUpdater[] getStyledTextUpdaters()
	{
		
		return null;
	}
	
	
	
}
