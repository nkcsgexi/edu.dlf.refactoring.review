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
import edu.dlf.refactoring.utils.XList;


public class JavaModelAnalyzer {
	
	private static Logger log = ServiceLocator.ResolveType(Logger.class);
	
	private JavaModelAnalyzer() throws Exception
	{
		throw new Exception();
	}
	
	public static XList<IJavaElement> getSourcePackages(IJavaElement project)
	{
		XList<IPackageFragment> packages;
		try {
			packages = new XList<IPackageFragment>
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
			return new XList<IJavaElement>();
		}
	}
	
	public static XList<IJavaElement> getCompilationUnits(IJavaElement packageEle)
	{
		XList<ICompilationUnit> list;
		try {
			list = new XList<ICompilationUnit>(
					((IPackageFragment) packageEle).getCompilationUnits());
			return list.cast(IJavaElement.class);
		} catch (Exception e) {
			log.fatal(e);
			return new XList<IJavaElement>();
		}
	}
	
	public static XList<IJavaElement> getTypes(IJavaElement cu)
	{
		XList<IJavaElement> list = XList.CreateList();
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
	
	public static XList<IJavaElement> getMethods(IJavaElement type)
	{
		try {
			XList<IJavaElement> list = new XList<IJavaElement>(((IType) type).getMethods());
			return list;
		} catch (Exception e) {
			log.fatal(e);
			return XList.CreateList();
		}
	}
	
	public static XList<IJavaElement> getFields(IJavaElement type)
	{
		try {
			return new XList<IJavaElement>(((IType)type).getFields());
		} catch (Exception e) {
			log.fatal(e);
			return XList.CreateList();
		}
	}
}



