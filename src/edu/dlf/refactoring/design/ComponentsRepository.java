package edu.dlf.refactoring.design;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.ServiceLocator.ChangeCompAnnotation;
import edu.dlf.refactoring.design.ServiceLocator.HidingCompAnnotation;
import edu.dlf.refactoring.design.ServiceLocator.HistorySavingCompAnnotation;
import edu.dlf.refactoring.design.ServiceLocator.UICompAnnotation;

public class ComponentsRepository {
	
	private final IFactorComponent changeComp;
	private final IFactorComponent UIComp;
	private final IFactorComponent hisComp;
	private final IFactorComponent hideComp;

	@Inject
	public ComponentsRepository(
		@HistorySavingCompAnnotation IFactorComponent hisComp,
		@ChangeCompAnnotation IFactorComponent changeComp,
		@UICompAnnotation IFactorComponent UIComp,
		@HidingCompAnnotation IFactorComponent hideComp)
	{
		this.hisComp = hisComp;
		this.changeComp = changeComp;
		this.UIComp = UIComp;
		this.hideComp = hideComp;
	}
	
	public IFactorComponent getChangeComponent()
	{
		return this.changeComp;
	}

	public IFactorComponent getUIComponent() {
		return this.UIComp;
	}

	public IFactorComponent getHistoryComp() {
		return this.hisComp;
	}
	
	public IFactorComponent getHidingComp()
	{
		return this.hideComp;
	}

}
