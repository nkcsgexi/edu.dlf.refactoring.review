package edu.dlf.refactoring.design;


import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import edu.dlf.refactoring.change.ChangeComponentInjector;
import edu.dlf.refactoring.checkers.ExtractMethodChecker;
import edu.dlf.refactoring.checkers.RenameMethodChecker;
import edu.dlf.refactoring.checkers.RenameTypeChecker;
import edu.dlf.refactoring.design.RefactoringAnnotations.ExtractMethod;
import edu.dlf.refactoring.design.RefactoringAnnotations.RenameMethod;
import edu.dlf.refactoring.design.RefactoringAnnotations.RenameType;
import edu.dlf.refactoring.detectors.ExtractMethodDetector;
import edu.dlf.refactoring.detectors.RenameMethodDetector;
import edu.dlf.refactoring.detectors.RenameTypeDetector;
import edu.dlf.refactoring.processors.ExtractMethodProcessor;
import edu.dlf.refactoring.processors.RenameMethodProcessor;
import edu.dlf.refactoring.processors.RenameTypeProcessor;

public class ServiceLocator extends AbstractModule
{
	private static AbstractModule _instance = new ServiceLocator();
	
	@Override
	protected void configure() {
		bind(IRefactoringDetector.class).annotatedWith(RenameMethod.class).to(RenameMethodDetector.class);
		bind(IRefactoringDetector.class).annotatedWith(ExtractMethod.class).to(ExtractMethodDetector.class);
		bind(IRefactoringDetector.class).annotatedWith(RenameType.class).to(RenameTypeDetector.class);
		
		bind(IRefactoringChecker.class).annotatedWith(RenameMethod.class).to(RenameMethodChecker.class);
		bind(IRefactoringChecker.class).annotatedWith(RenameType.class).to(RenameTypeChecker.class);
		bind(IRefactoringChecker.class).annotatedWith(ExtractMethod.class).to(ExtractMethodChecker.class);
		
		bind(RefactoringProcessor.class).annotatedWith(RenameMethod.class).to(RenameMethodProcessor.class);
		bind(RefactoringProcessor.class).annotatedWith(ExtractMethod.class).to(ExtractMethodProcessor.class);
		bind(RefactoringProcessor.class).annotatedWith(RenameType.class).to(RenameTypeProcessor.class);
		
		bind(EventBus.class).to(RefactoringEventBus.class).in(Singleton.class);
		
		this.install(new ChangeComponentInjector());
	}
	
	
	
	public static <T> T ResolveType (Class T)
	{		
		Injector injector = Guice.createInjector(_instance);
		return (T) injector.getInstance(T);
	}
	
	
	  @Provides 
	  @Singleton
	  private Logger GetLogger() throws Exception
	  {
		  Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		  ConsoleAppender console = new ConsoleAppender(); //create appender
		  String PATTERN = "%d [%p|%c|%C{1}] %m%n";
		  console.setLayout(new PatternLayout(PATTERN)); 
		  console.setThreshold(Level.DEBUG);
		  console.activateOptions();
		  Logger.getRootLogger().addAppender(console);
		
		  FileAppender fa = new FileAppender();
		  fa.setName("FileLogger");
		  fa.setFile("/home/xige/Desktop/RefReviewer.log", true, true, 1);
		  fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
		  fa.setThreshold(Level.DEBUG);
		  fa.setAppend(true);
		  fa.activateOptions();
	
		  Logger.getRootLogger().addAppender(fa);
		  
		  return Logger.getRootLogger();
	  }
}


















