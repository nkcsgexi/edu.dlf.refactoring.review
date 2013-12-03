package edu.dlf.refactoring.analyzers;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.dlf.refactoring.design.ServiceLocator;
import fj.Equal;
import fj.F;
import fj.F2;
import fj.Ord;
import fj.P;
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
	
	
	public static List<IProject> getAllProjectsInWorkSpace()
	{
	    IWorkspace workspace = ResourcesPlugin.getWorkspace();
	    IWorkspaceRoot root = workspace.getRoot();
	    IProject[] projects = root.getProjects();
	    return FJUtils.createListFromArray(projects);
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
	
	public static final F<IJavaElement, String> getElementNameFunc = 
		new F<IJavaElement, String>() {
			@Override
			public String f(IJavaElement element) {
				return element == null ? "" : element.getElementName();
	}};
	
	
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
	
	public static IJavaElement getAssociatedICompilationUnit(ASTNode node)
	{
		CompilationUnit unit = (CompilationUnit)node.getRoot();
		return unit.getJavaElement();
	}
	
	
	public static List<IJavaElement> getAssociatedITypes(final ASTNode node)
	{
		List<IJavaElement> types = getTypes(((CompilationUnit)node.getRoot()).
			getJavaElement());
		return types.filter(new F<IJavaElement, Boolean>() {
			@Override
			public Boolean f(IJavaElement type) {
				ISourceRange range = getJavaElementSourceRange(type);
				if(range.getOffset() <= node.getStartPosition() && 
					range.getOffset() + range.getLength() >= node.
						getStartPosition() + node.getLength())
				{
					logger.debug(type.getElementName() + ":" + range.getLength());
					return true;
				}
				else
					return false;
			}});
	}
	
	public static Ord<IJavaElement> getJavaElementLengthOrd()
	{
		return Ord.intOrd.comap(new F<IJavaElement, Integer>() {
			@Override
			public Integer f(IJavaElement element) {
				int length = getJavaElementSourceRange(element).getLength();
				logger.debug(element.getElementName() + ":" + length); 
				return length;
			}
		});
	}
	
	
	
	public static List<IJavaElement> getTypes(IJavaElement iu)
	{
		try {
			ICompilationUnit unit = (ICompilationUnit)iu;
			List<IJavaElement> results = List.nil();
			for(IJavaElement t : unit.getTypes()) {
				results = results.snoc(t);
			}
			return results;
		}catch(Exception e )
		{
			logger.fatal(e);
			return List.nil();
		}
	}
	
	public static List<IJavaElement> getMethods(IJavaElement t)
	{
		try{
			List<IJavaElement> methods = List.nil();
			IType type = (IType) t;
			for(IJavaElement m : type.getMethods())
			{
				methods = methods.snoc(m);
			}
			return methods;
		}catch(Exception e )
		{
			logger.fatal(e);
			return List.nil();
		}
	}
	
	public static List<IJavaElement> getFields(IJavaElement t)
	{
		try{
			List<IJavaElement> fields = List.nil();
			IType type = (IType) t;
			for(IJavaElement f : type.getFields())
			{
				fields = fields.snoc(f);
			}
			return fields;
		}catch(Exception e )
		{
			logger.fatal(e);
			return List.nil();
		}
	}
	
	public static List<IJavaElement> getOverlapMembers(ASTNode node)
	{
		final int start = node.getStartPosition();
		final int length = node.getLength();
		CompilationUnit cu = (CompilationUnit) node.getRoot();
		IJavaElement unit = cu.getJavaElement();
		
		List<IJavaElement> allElements = getTypes(unit).bind(
			new F<IJavaElement, List<IJavaElement>>() {
			@Override
			public List<IJavaElement> f(IJavaElement type) {
				return getMethods(type).append(getFields(type));
			}
		});
		
		return allElements.filter(new F<IJavaElement, Boolean>() {
			@Override
			public Boolean f(IJavaElement element) {
				ISourceRange range = getJavaElementSourceRange(element);
				if(range != null)
				{
					return !(range.getOffset() + range.getLength() < start || 
						start + length < range.getOffset());
				}
				return false;
			}
		});
	}
	
	
	public static ISourceRange getJavaElementSourceRange(IJavaElement element)
	{
		try{
			if(element instanceof ISourceReference)
			{
				ISourceReference reference = (ISourceReference) element;
				return reference.getSourceRange();
			}
			return null;
		}catch(Exception e)
		{
			logger.fatal(e);
			return null;
		}
	}
	
	
	public static List<P2<IJavaElement, IJavaElement>> getSimilarJavaElement(
			List<IJavaElement> list1, List<IJavaElement> list2, final int minimumScore,
			final F2<IJavaElement, IJavaElement, Integer> similarScoreCalculator)
	{
		List<P2<IJavaElement, IJavaElement>> allTuples = list1.bind(list2, 
			new F2<IJavaElement, IJavaElement, P2<IJavaElement, IJavaElement>>(){
			@Override
			public P2<IJavaElement, IJavaElement> f(IJavaElement arg0,
					IJavaElement arg1) {
				return P.p(arg0, arg1);
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
						return P.p(arg0._1(), arg0._2().some());
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

	public static String getQualifiedTypeName(IJavaElement element) {
		return ((IType)element).getFullyQualifiedName();
	}
}



