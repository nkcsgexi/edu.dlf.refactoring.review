package edu.dlf.refactoring.implementer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.MethodChecks;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameCompilationUnitProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameEnumConstProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameFieldProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameJavaProjectProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameLocalVariableProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameNonVirtualMethodProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenamePackageProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameSourceFolderProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeParameterProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameTypeProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameVirtualMethodProcessor;
import org.eclipse.jdt.internal.corext.util.JdtFlags;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.jdt.ui.refactoring.RenameSupport;

import edu.dlf.refactoring.design.IRefactoringImplementer;

public abstract class AbstractRenameImplementer implements IRefactoringImplementer{

	private final int flag = 
			RenameSupport.UPDATE_REFERENCES|
			RenameSupport.UPDATE_GETTER_METHOD|
			RenameSupport.UPDATE_SETTER_METHOD;
	private final Logger logger;
	
	protected AbstractRenameImplementer(Logger logger)
	{
		this.logger = logger;
	}
	
	protected JavaRenameProcessor getRenameProcessor(IJavaElement element)
	{
		try{
			return getRenameProcessorInternal(element);
		}catch(Exception e)
		{		
			logger.fatal(e);
			return null;
		}
	}
	
	protected RenameRefactoring getRenameRefactoring(JavaRenameProcessor processor)
	{
		return new RenameRefactoring(processor);
	}
	
	private JavaRenameProcessor getRenameProcessorInternal(IJavaElement element) throws Exception
    {
            int eleType = element.getElementType();
            switch(eleType)
            {
            case IJavaElement.COMPILATION_UNIT:                     
                    ICompilationUnit unit = (ICompilationUnit) element;
                    return new RenameCompilationUnitProcessor(unit);

            case IJavaElement.FIELD:
                    IField field = (IField)element;
                    if (JdtFlags.isEnum(field))
                            return new RenameEnumConstProcessor(field);
                    else {
                            final RenameFieldProcessor RFprocessor= new RenameFieldProcessor(field);
                            RFprocessor.setRenameGetter((flag & RenameSupport.UPDATE_GETTER_METHOD) != 0);
                            RFprocessor.setRenameSetter((flag & RenameSupport.UPDATE_SETTER_METHOD) != 0);
                            return RFprocessor;
                    }

            case IJavaElement.JAVA_PROJECT:                 
                    IJavaProject project = (IJavaProject) element;
                    return new RenameJavaProjectProcessor(project);
    
            case IJavaElement.LOCAL_VARIABLE:       
                    ILocalVariable variable = (ILocalVariable) element;
                    RenameLocalVariableProcessor LVProcessor = new RenameLocalVariableProcessor(variable);
                    LVProcessor.setUpdateReferences((flag & RenameSupport.UPDATE_REFERENCES) != 0);
                    return LVProcessor;
                    
            case IJavaElement.METHOD:
                    final IMethod method= (IMethod)element;
                    JavaRenameProcessor MProcessor;
                    if (MethodChecks.isVirtual(method)) {
                            MProcessor= new RenameVirtualMethodProcessor(method);
                    } else {
                            MProcessor= new RenameNonVirtualMethodProcessor(method);
                    }
                    return MProcessor;
         
            case IJavaElement.PACKAGE_FRAGMENT:     
                    IPackageFragment fragment = (IPackageFragment) element;
                    return new RenamePackageProcessor(fragment);
                    
            case IJavaElement.PACKAGE_FRAGMENT_ROOT:
                    IPackageFragmentRoot root = (IPackageFragmentRoot) element;
                    return new RenameSourceFolderProcessor(root);
                    
            case IJavaElement.TYPE_PARAMETER:
                    ITypeParameter parameter = (ITypeParameter) element;
                    RenameTypeParameterProcessor TPProcessor= new RenameTypeParameterProcessor(parameter);
                    TPProcessor.setUpdateReferences((flag & RenameSupport.UPDATE_REFERENCES) != 0);
                    return TPProcessor;
                    
            case IJavaElement.TYPE:                 
                    IType type = (IType)element;
                    return new RenameTypeProcessor(type);
            
            default:
                    return null;
            }
    }
	
	
}
