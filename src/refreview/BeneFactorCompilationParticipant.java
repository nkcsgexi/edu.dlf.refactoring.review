package refreview;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;

import com.google.common.eventbus.EventBus;

import edu.dlf.refactoring.design.ServiceLocator;

public class BeneFactorCompilationParticipant extends CompilationParticipant {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final EventBus bus = ServiceLocator.ResolveType(EventBus.class);
	
	public BeneFactorCompilationParticipant() {
		super();
		
	}
	
	@Override
	public void reconcile(ReconcileContext context)
	{
		try {
			bus.post(context.getWorkingCopy());
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
