package refreview;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.design.ComponentsRepository;
import edu.dlf.refactoring.design.ServiceLocator;

public class BeneFactorCompilationParticipant extends CompilationParticipant {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final EventBus bus = new EventBus();
	
	public BeneFactorCompilationParticipant() {
		super();
		bus.register(((ComponentsRepository)ServiceLocator.ResolveType
				(ComponentsRepository.class)).getHistoryComp());
	}
	
	@Override
	public void reconcile(ReconcileContext context)
	{
		try {
			ICompilationUnit unit = context.getWorkingCopy();
			bus.post(unit);
		} catch (Exception e) {
			logger.fatal(e);
		}
	}
	
	@Override
	public boolean isActive(IJavaProject project)
	{
		return true;
	}

}
