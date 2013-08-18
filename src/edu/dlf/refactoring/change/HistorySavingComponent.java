package edu.dlf.refactoring.change;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.JavaElementPair;
import edu.dlf.refactoring.design.ServiceLocator;
import fj.Effect;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class HistorySavingComponent implements IFactorComponent {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final EventBus bus = ServiceLocator.ResolveType(EventBus.class);
	private final HashMap<String, Buffer<ICompilationUnit>> history = 
			new HashMap<String, Buffer<ICompilationUnit>>();
	
	@Inject
	public HistorySavingComponent()
	{
		
	}
	
	
	
	@Override
	public Void listen(Object event)
	{
		if(event instanceof ICompilationUnit)
		{
			ICompilationUnit iu = (ICompilationUnit) event;
			String path = getPath(iu);
			if(path != null)
			{
				if(history.containsKey(path))
				{
					Buffer buffer = history.get(path);
					buffer.snoc(iu);
					HandleChange(buffer.toList().reverse());
				}
				else
				{
					Buffer buffer = Buffer.empty();
					buffer.snoc(iu);
					this.history.put(path, buffer);
				}
			}
		}
		return null;
	}
	
	private void HandleChange(List<ICompilationUnit> changeList) {
		final ICompilationUnit after = changeList.head();
		changeList.splitAt(1)._2().map(new F<ICompilationUnit, JavaElementPair>(){
			@Override
			public JavaElementPair f(ICompilationUnit before) {
				return new JavaElementPair(before, after);
			}}).foreach(new Effect<JavaElementPair>(){
				@Override
				public void e(JavaElementPair pair) {
					bus.post(pair);
				}});
	}

	private String getPath(ICompilationUnit iu)
	{
		try{
			IResource resource = iu.getResource();
			IFile ifile = (IFile) resource;
			String path = ifile.getRawLocation().toString();
			return path;
		}catch (Exception e)
		{
			logger.fatal(e);
			return null;
		}
	}
}
