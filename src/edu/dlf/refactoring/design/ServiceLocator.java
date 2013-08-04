package edu.dlf.refactoring.design;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.dlf.refactoring.checkers.ExtractMethodChecker;
import edu.dlf.refactoring.checkers.RenameMethodChecker;
import edu.dlf.refactoring.checkers.RenameTypeChecker;
import edu.dlf.refactoring.design.RefactoringAnnotations.ExtractMethod;
import edu.dlf.refactoring.design.RefactoringAnnotations.RenameMethod;
import edu.dlf.refactoring.design.RefactoringAnnotations.RenameType;
import edu.dlf.refactoring.detectors.ExtractMethodDetector;
import edu.dlf.refactoring.detectors.RenameMethodDetector;
import edu.dlf.refactoring.detectors.RenameTypeDetector;

public class ServiceLocator extends AbstractModule
{
	@Override
	protected void configure() {
		bind(IRefactoringDetector.class).annotatedWith(RenameMethod.class).to(RenameMethodDetector.class);
		bind(IRefactoringDetector.class).annotatedWith(ExtractMethod.class).to(ExtractMethodDetector.class);
		bind(IRefactoringDetector.class).annotatedWith(RenameType.class).to(RenameTypeDetector.class);
		
		bind(IRefactoringChecker.class).annotatedWith(RenameMethod.class).to(RenameMethodChecker.class);
		bind(IRefactoringChecker.class).annotatedWith(RenameType.class).to(RenameTypeChecker.class);
		bind(IRefactoringChecker.class).annotatedWith(ExtractMethod.class).to(ExtractMethodChecker.class);
	}
	
	public static <T> T ResolveType (Class T)
	{		
		Injector injector = Guice.createInjector(new ServiceLocator());
		return (T) injector.getInstance(T);
	}
	
}
