package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.dlf.refactoring.design.ICompListener;
import edu.dlf.refactoring.design.IDetectedRefactoring;
import edu.dlf.refactoring.design.IFactorComponent;
import edu.dlf.refactoring.design.IImplementedRefactoring;
import edu.dlf.refactoring.design.IRefactoringImplementer;
import edu.dlf.refactoring.design.RefactoringType;
import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.ExtractMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameMethod;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector.RenameType;
import fj.F;
import fj.data.HashMap;

public class RefactoringImplementerComponent implements IFactorComponent{
	
	private final Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private final LoadingCache<IDetectedRefactoring, IImplementedRefactoring> 
		implementedRefactoringCache;
	
	private final F<IDetectedRefactoring, IImplementedRefactoring> 
		implementRefactoringFunc;
	
	private final HashMap<RefactoringType, IRefactoringImplementer> 
		implementers;

	public RefactoringImplementerComponent(
			@ExtractMethod final IRefactoringImplementer emImplementer,
			@RenameMethod final IRefactoringImplementer rmImplementer,
			@RenameType final IRefactoringImplementer rtImplementer)
	{
		this.implementers = HashMap.hashMap();
		this.implementers.set(RefactoringType.ExtractMethod, emImplementer);
		this.implementers.set(RefactoringType.RenameMethod, rmImplementer);
		this.implementers.set(RefactoringType.RenameType, rtImplementer);
		
		this.implementRefactoringFunc = new F<IDetectedRefactoring, 
				IImplementedRefactoring>(){
			@Override
			public IImplementedRefactoring f(IDetectedRefactoring detected) {
				return implementers.get(detected.getRefactoringType()).some().
						implementRefactoring(detected).some();
			}};
		
		
		this.implementedRefactoringCache =  CacheBuilder.newBuilder()
	       .maximumSize(1000).build(new CacheLoader<IDetectedRefactoring, 
	    		   IImplementedRefactoring>() {
			@Override
			public IImplementedRefactoring load(IDetectedRefactoring detected) {
				return implementRefactoringFunc.f(detected);
			}});
	}
	
	
	@Override
	public Void listen(Object event) {
		try {
			this.implementedRefactoringCache.get((IDetectedRefactoring)event);
		} catch (Exception e) {
			logger.fatal(e);
		}
		return null;
	}


	@Override
	public Void registerListener(ICompListener listener) {
		// TODO Auto-generated method stub
		return null;
	}
}
