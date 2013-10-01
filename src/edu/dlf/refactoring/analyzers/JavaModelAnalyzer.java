package edu.dlf.refactoring.analyzers;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.F;
import fj.F2;
import fj.P2;
import fj.data.List;
import fj.data.List.Buffer;
import fj.data.Option;


public class JavaModelAnalyzer {
	
	private static Logger logger = ServiceLocator.ResolveType(Logger.class);
	
	private JavaModelAnalyzer() throws Exception
	{
		throw new Exception();
	}
	
	public static List<IJavaElement> getJavaProjectsInWorkSpace()
	{
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();
	    Buffer<IJavaElement> buffer = Buffer.empty();
	    for(IProject p : projects)
	    {
	    	try{
	    		if (p.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
	    			buffer.snoc(JavaCore.create(p));
	    		}
	    	}catch(Exception e)
	    	{
	    		logger.fatal(e);
	    		continue;
	    	}
	    }
	    return buffer.toList();
	}
	
	
	public static List<IJavaElement> getSourcePackages(IJavaElement project)
	{
		try{
			List<IJavaElement> list = List.nil();
			IPackageFragment[] packages = ((IJavaProject)project).
				getPackageFragments();
		    for (IPackageFragment pac : packages) {
		      if (pac.getKind() == IPackageFragmentRoot.K_SOURCE) {
		    	  list = list.snoc(pac);
		      }
		    }
		    return list;
	    }catch(Exception e) {
			logger.fatal(e);
			return List.nil();
		}
	}
	
	public static List<IJavaElement> getICompilationUnit(IJavaElement pack)
	{
		try{
			List<IJavaElement> list = List.nil();
			for (ICompilationUnit unit : ((IPackageFragment)pack).
				getCompilationUnits()) {
				list = list.snoc(unit);
			}
			return list;
		}catch(Exception e)
		{
			logger.fatal(e);
			return List.nil();
		}
	}
	
	public static F2<List<IJavaElement>, List<IJavaElement>, 
		List<P2<IJavaElement, IJavaElement>>> getSameNameElementPairsFunction()
	{
		return new F2<List<IJavaElement>, List<IJavaElement>, List<P2<IJavaElement,
				IJavaElement>>>() {
			@Override
			public List<P2<IJavaElement, IJavaElement>> f(List<IJavaElement> list1,
					final List<IJavaElement> list2) {
				return list1.zip(list1.map(new F<IJavaElement, Option<IJavaElement>>() {
					@Override
					public Option<IJavaElement> f(final IJavaElement arg0) {
						return list2.find(new F<IJavaElement, Boolean>() {
							@Override
							public Boolean f(IJavaElement arg1) {
								return arg0.getElementName().equals(arg1.
										getElementName());
							}
						});
					}
				})).filter(new F<P2<IJavaElement,Option<IJavaElement>>, Boolean>() {
					@Override
					public Boolean f(P2<IJavaElement, Option<IJavaElement>> arg0) {
						return arg0._2().isSome();
					}
				}).map(new F<P2<IJavaElement,Option<IJavaElement>>, P2<IJavaElement, 
						IJavaElement>>() {
					@Override
					public P2<IJavaElement, IJavaElement> f(
							P2<IJavaElement, Option<IJavaElement>> arg0) {
						return List.single(arg0._1()).zip(List.single(arg0._2().
							some())).head();
					}
				});
			}
		};
	}
	
	
	public static F2<List<IJavaElement>, List<IJavaElement>, List<IJavaElement>>
		getAddedElementsFunction()
	{
		return new F2<List<IJavaElement>, List<IJavaElement>, List<IJavaElement>>() {
			@Override
			public List<IJavaElement> f(final List<IJavaElement> list1, 
					List<IJavaElement> list2) {
				return list2.filter(new F<IJavaElement, Boolean>(){
					@Override
					public Boolean f(final IJavaElement element) {
						return list1.forall(new F<IJavaElement, Boolean>() {
							@Override
							public Boolean f(IJavaElement arg0) {
								return element.getElementName().equals(arg0);
							}
						});
					}});
			}
		};
	}
	
	public static F2<List<IJavaElement>, List<IJavaElement>, List<IJavaElement>>
		getRemovedElementsFunction()
	{
		return new F2<List<IJavaElement>, List<IJavaElement>, List<IJavaElement>>() {
			@Override
			public List<IJavaElement> f(List<IJavaElement> list1, final 
					List<IJavaElement> list2) {
				return list1.filter(new F<IJavaElement, Boolean>() {
					@Override
					public Boolean f(final IJavaElement arg0) {
						return list2.forall(new F<IJavaElement, Boolean>() {
							@Override
							public Boolean f(IJavaElement arg1) {
								return arg0.getElementName().equals(arg1.
										getElementName());
							}
						});
					}
				});
			}
		};
	}
}



