package edu.dlf.refactoring.change;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import edu.dlf.refactoring.analyzers.ASTAnalyzer;
import edu.dlf.refactoring.design.ASTNodePair;
import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.design.ServiceLocator.ChangeCompAnnotation;
import fj.Effect;
import fj.F;
import fj.data.List;
import fj.data.List.Buffer;

public class HistorySavingComponent implements IFactorComponent {

	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	private final HashMap<String, ArrayList<ASTNode>> history = 
			new HashMap<String, ArrayList<ASTNode>>();
	private final EventBus bus;
	
	@Inject
	public HistorySavingComponent(
			@ChangeCompAnnotation IFactorComponent component)
	{
		this.bus = new EventBus();
		this.bus.register(component);
	}

	@Subscribe
	@Override
	public Void listen(Object event)
	{
		try {
			if(event instanceof ICompilationUnit)
			{
				logger.info("Get event.");
				ICompilationUnit iu = (ICompilationUnit) event;
				ASTNode cu = ASTAnalyzer.parseICompilationUnit(iu);
				String path = getPath(iu);
				if(path != null)
				{
					if(history.containsKey(path))
					{
						ArrayList<ASTNode> list = history.get(path);
						if(!ASTAnalyzer.areASTNodesSame(cu, list.get(0))){
							list.add(0, cu);
							HandleChange(ToFunctionalList(list));
						}
					}
					else
					{
						ArrayList<ASTNode> list = new ArrayList<ASTNode>();
						list.add(cu);
						this.history.put(path, list);
					}
				}
				logger.info("Handled event.");
			}
		}catch(Exception e)
		{
			logger.fatal(e);
		}
		return null;
	}
	
	List<ASTNode> ToFunctionalList(ArrayList<ASTNode> list)
	{
		Buffer<ASTNode> buffer = Buffer.empty();
		for(ASTNode n : list)
		{
			buffer.snoc(n);
		}
		return buffer.toList();
	}
	
	private void HandleChange(List<ASTNode> changeList) {
		final ASTNode after = changeList.head();
		changeList.splitAt(1)._2().map(new F<ASTNode, ASTNodePair>(){
			@Override
			public ASTNodePair f(ASTNode before) {
				return new ASTNodePair(before, after);
			}}).foreach(new Effect<ASTNodePair>(){
				@Override
				public void e(ASTNodePair pair) {
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

	@Override
	public Void registerListener(ICompListener listener) {
		this.bus.register(listener);
		return null;
	}
}
