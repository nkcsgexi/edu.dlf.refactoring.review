package edu.dlf.refactoring.design;

import com.google.inject.Inject;

import edu.dlf.refactoring.design.ServiceLocator.ChangeCompAnnotation;
import edu.dlf.refactoring.design.ServiceLocator.HistorySavingCompAnnotation;
import edu.dlf.refactoring.design.ServiceLocator.UICompAnnotation;

public class ComponentsRepository {
	
	private final IFactorComponent changeComp;
	private final IFactorComponent UIComp;
	private final IFactorComponent hisComp;

	@Inject
	public ComponentsRepository(
		@HistorySavingCompAnnotation IFactorComponent hisComp,
		@ChangeCompAnnotation IFactorComponent changeComp,
		@UICompAnnotation IFactorComponent UIComp)
	{
		this.hisComp = hisComp;
		this.changeComp = changeComp;
		this.UIComp = UIComp;
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

}
