package edu.dlf.refactoring.analyzers;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.base.Predicate;

import edu.dlf.refactoring.design.ServiceLocator;
import edu.dlf.refactoring.utils.XArrayList;


public class JavaModelAnalyzer {
	
	private static Logger log = ServiceLocator.ResolveType(Logger.class);
	
	private JavaModelAnalyzer() throws Exception
	{
		throw new Exception();
	}
	
	public static XArrayList<IJavaElement> getSourcePackages(IJavaElement project)
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
	
	public static XArrayList<IJavaElement> getCompilationUnits(IJavaElement packageEle)
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
	
	public static XArrayList<IJavaElement> getTypes(IJavaElement cu)
	{
		XArrayList<IJavaElement> list = XArrayList.CreateList();
		try {
			IType[] types = ((ICompilationUnit)cu).getAllTypes();
			for(IJavaElement type : types)
			{
				list.add(type);
			}
			return list;
		} catch (Exception e) {
			log.fatal(e);
			return list;
		}
	}
	
	public static XArrayList<IJavaElement> getMethods(IJavaElement type)
	{
		try {
			XArrayList<IJavaElement> list = new XArrayList<IJavaElement>(((IType) type).getMethods());
			return list;
		} catch (Exception e) {
			log.fatal(e);
			return XArrayList.CreateList();
		}
	}
	
	public static XArrayList<IJavaElement> getFields(IJavaElement type)
	{
		try {
			return new XArrayList<IJavaElement>(((IType)type).getFields());
		} catch (Exception e) {
			log.fatal(e);
			return XArrayList.CreateList();
		}
	}
}



