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
import edu.dlf.refactoring.detectors.RefactoringDetectionComponentInjector;

public class ServiceLocator extends AbstractModule
{
	private static AbstractModule _instance = new ServiceLocator();
	
	@Override
	protected void configure() {
		bind(EventBus.class).to(RefactoringEventBus.class).in(Singleton.class);
		this.install(new ChangeComponentInjector());
		this.install(new RefactoringDetectionComponentInjector());
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


















