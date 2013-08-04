package edu.dlf.refactoring.analyzers;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.google.common.base.Predicate;

import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XArrayList;


public class JavaProjectAnalyzer {
	
	private static Logger log = ServiceLocator.ResolveType(Logger.class);
	
	public static XArrayList<IJavaElement> GetSourcePackages(IJavaElement project)
	{
		XArrayList<IPackageFragment> packages;
		try {
			packages = new XArrayList<IPackageFragment>
				(((IJavaProject)project).getPackageFragments());
			return packages.where(new Predicate<IPackageFragment>(){
				@Override
				public boolean apply(IPackageFragment fragment) {
					try {
						return fragment.getKind() == IPackageFragmentRoot.K_SOURCE;
					} catch (Exception e) {
						log.fatal(e);
						return false;
					}
				}}).cast(IJavaElement.class);
		} catch (Exception e) {
			log.fatal(e);
			return new XArrayList<IJavaElement>();
		}
	}
	
	public static XArrayList<IJavaElement> GetCompilationUnits(IJavaElement packageEle)
	{
		XArrayList<ICompilationUnit> list;
		try {
			list = new XArrayList<ICompilationUnit>(
					((IPackageFragment) packageEle).getCompilationUnits());
			return list.cast(IJavaElement.class);
		} catch (Exception e) {
			log.fatal(e);
			return new XArrayList<IJavaElement>();
		}
	}
	

}
