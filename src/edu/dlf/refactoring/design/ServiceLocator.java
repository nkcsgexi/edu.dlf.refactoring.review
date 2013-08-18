package edu.dlf.refactoring.design;


import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import edu.dlf.refactoring.change.ChangeComponent;
import edu.dlf.refactoring.change.ChangeComponentInjector;
import edu.dlf.refactoring.change.HistorySavingComponent;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponent;
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector;

public class ServiceLocator extends AbstractModule
{
	private final static AbstractModule _instance = new ServiceLocator();
	
	
	@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
	public @interface HistorySavingCompAnnotation {}
	
	@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
	public @interface ChangeCompAnnotation {}
	
	@BindingAnnotation @Target({ FIELD, PARAMETER, METHOD, CONSTRUCTOR }) @Retention(RUNTIME)
	public @interface RefactoringDetectionCompAnnotation {}
	
	
	
	private ServiceLocator()
	{
		
	}
	
	@Override
	protected void configure() {
		
		this.install(new ChangeComponentInjector());
		this.install(new RefactoringDetectionComponentInjector());
		
		bind(HistorySavingComponent.class).in(Singleton.class);
		bind(ChangeComponent.class).in(Singleton.class);
		bind(RefactoringDetectionComponent.class).in(Singleton.class);
		
		bind(IFactorComponent.class).annotatedWith(HistorySavingCompAnnotation.class).to(HistorySavingComponent.class);
		bind(IFactorComponent.class).annotatedWith(ChangeCompAnnotation.class).to(ChangeComponent.class);
		bind(IFactorComponent.class).annotatedWith(RefactoringDetectionCompAnnotation.class).to(RefactoringDetectionComponent.class);
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
		  console.setThreshold(Level.ALL);
		  console.activateOptions();
		  Logger.getRootLogger().addAppender(console);
		
		  RollingFileAppender fa = new RollingFileAppender();
		  fa.setImmediateFlush(true);
		  fa.setMaximumFileSize(Integer.MAX_VALUE);
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


















