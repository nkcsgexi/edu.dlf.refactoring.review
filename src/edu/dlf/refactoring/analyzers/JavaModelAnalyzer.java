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
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
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
	
	public static Equal<IJavaElement> getJavaElementEQ()
	{
		 return Equal.equal(new F<IJavaElement, F<IJavaElement, 
			Boolean>>(){
			@Override
			public F<IJavaElement, Boolean> f(final IJavaElement ele1) {
				return new F<IJavaElement, Boolean>() {
					@Override
					public Boolean f(final IJavaElement ele2) {
						return ele1 == ele2;
					}
				};
			}});
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
		    return list.filter(new F<IJavaElement, Boolean>() {
				@Override
				public Boolean f(IJavaElement pack) {
					return getICompilationUnit(pack).isNotEmpty();
				}
			});
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
	
	
	public static List<P2<IJavaElement, IJavaElement>> getSimilaryJavaElement(
			List<IJavaElement> list1, List<IJavaElement> list2, final int minimumScore,
			final F2<IJavaElement, IJavaElement, Integer> similarScoreCalculator)
	{
		List<P2<IJavaElement, IJavaElement>> allTuples = list1.bind(list2, 
			new F2<IJavaElement, IJavaElement, P2<IJavaElement, IJavaElement>>(){
			@Override
			public P2<IJavaElement, IJavaElement> f(IJavaElement arg0,
					IJavaElement arg1) {
				return List.single(arg0).zip(List.single(arg1)).head();
			}});
		
		List<Integer> allScores = allTuples.map(new F<P2<IJavaElement,
				IJavaElement>, Integer>() {
			@Override
			public Integer f(P2<IJavaElement, IJavaElement> p) {
				return similarScoreCalculator.f(p._1(), p._2());
			}
		});
		
		Ord<P2<P2<IJavaElement, IJavaElement>, Integer>> ord = Ord.intOrd.comap
				(new F<P2<P2<IJavaElement, IJavaElement>, Integer>, Integer>(){
			@Override
			public Integer f(P2<P2<IJavaElement, IJavaElement>, Integer> p) {
				return p._2();
			}});
		
		List<P2<IJavaElement, IJavaElement>> matchedTuples = allTuples.zip(allScores).
			filter(new F<P2<P2<IJavaElement,IJavaElement>, Integer>, Boolean>() {
			@Override
			public Boolean f(P2<P2<IJavaElement, IJavaElement>, Integer> arg0) {
				return arg0._2() >= minimumScore;
			}
		}).sort(ord).reverse().map(new F<P2<P2<IJavaElement,IJavaElement>,Integer>, 
				P2<IJavaElement, IJavaElement>>() {
			@Override
			public P2<IJavaElement, IJavaElement> f(
					P2<P2<IJavaElement, IJavaElement>, Integer> arg0) {
				return arg0._1();
			}});
		
		return matchedTuples.nub(Equal.equal(new F<P2<IJavaElement, IJavaElement>, 
				F<P2<IJavaElement, IJavaElement>, Boolean>>(){
			@Override
			public F<P2<IJavaElement, IJavaElement>, Boolean> f(final P2<IJavaElement, 
				IJavaElement> p1) {
				return new F<P2<IJavaElement,IJavaElement>, Boolean>() {
					@Override
					public Boolean f(P2<IJavaElement, IJavaElement> p2) {
						return p1._1() == p2._1() || p1._2() == p2._2();
					}
				};}}));
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
								return !element.getElementName().equals(arg0);
							}});
					}});}};
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
								return !arg0.getElementName().equals(arg1.
									getElementName());
							}});}
				});}};
	}

	public static Boolean arePathsSame(IJavaElement unit1, IJavaElement unit2) {
		String p1 = unit1.getPath().toOSString();
		String p2 = unit2.getPath().toOSString();
		return p1.equals(p2);
	}
}



